package learn.lhb.security.oauth2.backend.controller;

import learn.lhb.security.oauth2.backend.dto.BaseResult;
import learn.lhb.security.oauth2.backend.dto.LoginInfo;
import learn.lhb.security.oauth2.backend.dto.LoginParam;
import learn.lhb.security.oauth2.backend.utils.MapperUtils;
import learn.lhb.security.oauth2.backend.utils.OkHttpClientUtil;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author 梁鸿斌
 * @date 2020/3/23.
 * @time 12:18
 */
@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin
@RestController
@RequestMapping("user")
public class LoginController {

    private static final String URL_OAUTH_TOKEN = "http://localhost:12345/oauth/token";

    @Value("${permission.oauth2.grant_type}")
    public String oauth2GrantType;

    @Value("${permission.oauth2.client_id}")
    public String oauth2ClientId;

    @Value("${permission.oauth2.client_secret}")
    public String oauth2ClientSecret;

    @Resource(name = "userDetailsService")
    private UserDetailsService userDetailsService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private TokenStore tokenStore;


    @PostMapping("login")
    public BaseResult login(@RequestBody LoginParam loginParam) {
        System.out.println("登录");
        System.out.println(loginParam.toString());
        // 封装返回结果集
        Map<String, Object> result = new HashMap<>();

        // 验证账户密码
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginParam.getUsername());
        if (userDetails == null || !passwordEncoder.matches(loginParam.getPassword(), userDetails.getPassword())) {
            return BaseResult.error("账户或者密码错误");
        }

        // 通过 HTTP客户端请求登录接口
        Map<String, String> params = new HashMap<>();
        params.put("username", loginParam.getUsername());
        params.put("password", loginParam.getPassword());
        params.put("grant_type", oauth2GrantType);
        params.put("client_id", oauth2ClientId);
        params.put("client_secret", oauth2ClientSecret);
        try {
            // 解析响应结果封装并返回
            Response response = OkHttpClientUtil.getInstance().postData(URL_OAUTH_TOKEN, params);
            String jsonString = Objects.requireNonNull(response.body()).string();
            Map<String, Object> jsonMap = MapperUtils.json2map(jsonString);
            String token = String.valueOf(jsonMap.get("access_token"));
            result.put("token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BaseResult().put(BaseResult.CodeStatus.OK, "登录成功", "data", result);
    }

    /**
     * 获取用户信息
     *
     * @param authentication
     * @return
     */
    @CrossOrigin
    @GetMapping(value = "info")
    public BaseResult info(Authentication authentication) throws Exception {
        System.out.println("获取用户信息");
        String jsonString = authentication.getName();

        System.out.println(jsonString);
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setName(authentication.getName());
        loginInfo.setAvatar("");
        loginInfo.setRoles("USER");

        return BaseResult.ok().put(BaseResult.CodeStatus.OK, "获取用户信息成功！", "data", loginInfo);
    }

    @PostMapping("logout")
    public BaseResult logout(HttpServletRequest request) {
        String token = request.getParameter("access_token");
        // readAccessToken读取token，removeAccessToken清除token
        tokenStore.removeAccessToken(tokenStore.readAccessToken(token));
        return BaseResult.ok("用户退出成功!");
    }
}
