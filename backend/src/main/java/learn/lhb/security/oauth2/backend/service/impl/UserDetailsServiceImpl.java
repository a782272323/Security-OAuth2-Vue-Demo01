package learn.lhb.security.oauth2.backend.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义认证授权
 *
 * @author 梁鸿斌
 * @date 2020/3/23.
 * @time 11:11
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "$2a$10$XFSfCISi8m1IVURYQt7u5OE1aqiNfkCkCYsD4AF/nqzrG7vUaRXUu";
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        // 用户名匹配
        if (s.equals(USERNAME)) {
            List<GrantedAuthority> grantedAuthorities = new LinkedList<>();
            // 授权
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
            grantedAuthorities.add(grantedAuthority);
            return new User(USERNAME, PASSWORD, grantedAuthorities);
        }
        // 不匹配
        else {
            return null;
        }

    }
}
