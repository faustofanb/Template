package fausto.fan.project.frameworks.starter.user.toolkit

import com.alibaba.fastjson2.JSON
import fausto.fan.project.framework.starter.base.Slf4j
import fausto.fan.project.framework.starter.base.Slf4j.Companion.log
import fausto.fan.project.framework.starter.base.constant.REAL_NAME_KEY
import fausto.fan.project.framework.starter.base.constant.USER_ID_KEY
import fausto.fan.project.framework.starter.base.constant.USER_NAME_KEY
import fausto.fan.project.frameworks.starter.user.core.UserInfoDTO
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

/**
 * JWT工具类，用于生成和解析JSON Web Token。
 */
@Slf4j
object JWTUtil {
    // 定义令牌的过期时间1天，单位为秒
    private const val EXPIRATION = 86400L

    // 定义令牌的前缀
    private const val TOKEN_PREFIX = "Bearer "

    // 定义JWT的颁发者
    private const val ISS = "project-template"

    // 定义JWT的秘钥
    const val SECRET = "SecretKey039245678901232039487623456783092349288901402967890140939827"

    /**
     * 生成访问令牌。
     *
     * @param userInfo 用户信息，包含用户ID、用户名和真实姓名。
     * @return 返回生成的访问令牌字符串。
     */
    fun generateAccessToken(userInfo: UserInfoDTO): String {
        return TOKEN_PREFIX + Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .setIssuedAt(Date())
            .setIssuer(ISS)
            .setSubject(
                JSON.toJSONString(
                    hashMapOf(
                        USER_ID_KEY to userInfo.userId,
                        USER_NAME_KEY to userInfo.username,
                        REAL_NAME_KEY to userInfo.realName
                    )
                )
            )
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION * 1000))
            .compact()
    }

    /**
     * 解析JWT令牌并返回用户信息。
     *
     * @param jwtToken JWT令牌字符串。
     * @return 如果令牌有效且未过期，则返回用户信息；否则返回null。
     */
    fun parseJwtToken(jwtToken: String): UserInfoDTO? {
        if (jwtToken.isNotBlank()) {
            val actualJwtToken = jwtToken.replace(TOKEN_PREFIX, "")
            try {
                val claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(actualJwtToken).body
                val expiration = claims.expiration
                if (expiration.after(Date())) {
                    return JSON.parseObject(claims.subject, UserInfoDTO::class.java)
                }
            } catch (ignored: ExpiredJwtException) {

            } catch (ex: Exception) {
                log.error("JWT Token解析失败，请检查", ex)
            }
        }
        return null
    }
}

