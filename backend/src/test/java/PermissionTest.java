import learn.lhb.security.oauth2.backend.OAuth2BackendApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author 梁鸿斌
 * @date 2020/3/23.
 * @time 11:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAuth2BackendApplication.class)
public class PermissionTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void passwordEncoderTest() {
        System.out.println(passwordEncoder.encode("123456"));
    }
}
