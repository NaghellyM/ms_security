package com.GSTCP.ms_security.Services;
//import com.GSTCP.ms_security.Models.Role;
import com.GSTCP.ms_security.Models.User;
//import com.GSTCP.ms_security.Repositories.SessionRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret; // Esta es la clave secreta que se utiliza para firmar el token. Debe mantenerse segura.

    @Value("${jwt.expiration}")
    private Long expiration; // Tiempo de expiración del token en milisegundos.
    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); //Esta secret key hace ya bien bien la llave

    public String generateToken(User theUser) {
        //Hace una fecha, ya que este expira
        Date now = new Date();
        //Da la expiracion del token
        Date expiryDate = new Date(now.getTime() + expiration);
        //Aqui creamos un diccionario, donde colocamos los datos del usuario
        Map<String, Object> claims = new HashMap<>();
        claims.put("_id", theUser.get_id());
        claims.put("name", theUser.getName());
        claims.put("email", theUser.getEmail());
        // claims.put("role", theUser.getRole());

        //Este return es la carga util del token
        return Jwts.builder()
                //El setClaims coloca la info del usuario
                .setClaims(claims)
                .setSubject(theUser.getName())
                //Aqui verifica cuando fue enviado el token
                .setIssuedAt(now)
                //Aqui da cuando expira el token
                .setExpiration(expiryDate)
                //Aqui es la firma con la palabra secreta que configuramos
                .signWith(secretKey)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            // Verifica la expiración del token
            Date now = new Date();
            if (claimsJws.getBody().getExpiration().before(now)) {
                return false;
            }

            return true;
        } catch (SignatureException ex) {
            // La firma del token es inválida
            return false;
        } catch (Exception e) {
            // Otra excepción
            return false;
        }
    }

    //Recibe un string y va a devolver un usuario
    public User getUserFromToken(String token) {
        try {

            //Esto lo que hace es descifrar el usuario
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey) //Con la llave
                    .build()
                    .parseClaimsJws(token);

            //Esto devuelve un objeto especial llamado "claims"
            //De aqui obtiene el body, que es la carga util del token
            Claims claims = claimsJws.getBody();

            //Aqui se crea el usuario y
            User user = new User();
            user.set_id((String) claims.get("_id")); //se le pone el identificador
            user.setName((String) claims.get("name")); //El nombre
            user.setEmail((String) claims.get("email")); //y el correo
            return user; //Y retorna ese usuario
        } catch (Exception e) {
            // En caso de que el token sea inválido o haya expirado
            return null;
        }
    }


}
