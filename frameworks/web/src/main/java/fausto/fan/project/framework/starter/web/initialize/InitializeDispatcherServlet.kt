package fausto.fan.project.framework.starter.web.initialize

import fausto.fan.project.framework.starter.base.Slf4j
import fausto.fan.project.framework.starter.base.Slf4j.Companion.log
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

// 定义初始化路径常量，用于统一调度器Servlet的初始化请求路径
const val INITIALIZE_PATH = "/initialize/dispatcher-servlet"

// 使用日志记录器，并通过RestController将类标记为Spring MVC控制器
@Slf4j
@RestController
class InitializeDispatcherServletController {

    // 处理GET请求，当访问INITIALIZE_PATH路径时，执行初始化操作
    @GetMapping(INITIALIZE_PATH)
    fun init(){
        // 记录dispatcherServlet初始化的日志，旨在优化接口首次响应时间
        log.info("Initialized the dispatcherServlet to improve the first response time of the interface...")
    }
}

// 初始化调度器Servlet的命令行运行时处理器，负责在应用启动时发起初始化请求
class InitializeDispatcherServletHandler(
    private val restTemplate: RestTemplate, // 用于执行HTTP请求的模板类
    private val configurableEnvironment: ConfigurableEnvironment // Spring Boot的可配置环境接口
): CommandLineRunner {
    // 在应用启动时执行CommandLineRunner的run方法，以发起初始化请求
    override fun run(vararg args: String?) {
        try {
            // 使用RestTemplate执行HTTP GET请求，目标是应用的初始化路径
            restTemplate.execute(
                "http://127.0.0.1:${
                    configurableEnvironment.getProperty("server.port", "8080") + 
                    configurableEnvironment.getProperty("server.servlet.context-path", "")
                }$INITIALIZE_PATH",
                HttpMethod.GET,
                null,
                null
            )
        } catch (ignored: Throwable){
            // 捕获并忽略执行过程中的所有异常，确保应用正常启动不受影响
        }
    }
}
