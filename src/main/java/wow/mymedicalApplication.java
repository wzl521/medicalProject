package wow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description
 * @autor wzl
 * @date 2021/3/30-20:23
 */
@SpringBootApplication
@MapperScan("wow.mapper")
public class mymedicalApplication {
    public static void main(String[] args) {
        SpringApplication.run(mymedicalApplication.class, args);
    }
}
