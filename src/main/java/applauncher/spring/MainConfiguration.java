package applauncher.spring;

import io.polyglotted.echoapp.EchoServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public EchoServer starter() {
        return new EchoServer();
    }
}
