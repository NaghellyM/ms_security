package com.ddcf.security.Services;



import com.ddcf.security.Models.Role;
import com.ddcf.security.Models.User;
import com.ddcf.security.Repositories.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class JwtService {

    @Autowired
    private SessionRepository theSessionRepository;
    //secreto token
    @Value("${jwt.secret}")//AQUI SE INYECTA EL VALOR DEL SECRET QUE ESTAN EN APPLICATION.PROPERTIES
    private String secret; // Esta es la clave secreta que se utiliza para firmar el token. Debe mantenerse segura.

    //expiracion token
    @Value("${jwt.expiration}")
    private Long expiration; // Tiempo de expiración del token en milisegundos.
    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    //llega el usuario como objeto y devolvera un String
    public String generateToken(User theUser) {
        //cree una fecha porque los token tiene una fecha de expiracion
        Date now = new Date();
        //se calcila la fecha de expiracion del token
        Date expiryDate = new Date(now.getTime() + expiration);//expiration es una constante del tiempo hasta que expire
        //se crea un diccionario y se coloca el id, nombre, contraseña tambien se puede meter el role del usuario
        Map<String, Object> claims = new HashMap<>(); //creamos un objeto lo llamamos clain y va a contener un Sting y un objeto
        claims.put("_id", theUser.get_id()); //ingresamos el id como String y theUser.get_id() es el id del objeto
        claims.put("name", theUser.getName());
        claims.put("email", theUser.getEmail());
        //claims.put("password", theUser.getPassword());
        //claims.put("role", theUser.getRole());


        return Jwts.builder()
                .setClaims(claims)//pasamos el contenido que firmamos osea el diccionario creamo anteriormente
                .setSubject(theUser.getName()) //cual es el nombre del usuario
                .setIssuedAt(now)//verificar cuando fue emitido el token
                .setExpiration(expiryDate)//cuando expira
                .signWith(secretKey)//llave si el token verifica si esta bien o  mal y firma si esta ok
                .compact();//compacta el token
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

    public User getUserFromToken(String token) { //recive string devuelve un usuario
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)//cifra con la firma del token
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            User user = new User();
            user.set_id((String) claims.get("_id"));
            user.setName((String) claims.get("name"));
            user.setEmail((String) claims.get("email"));
            //user.setRole((List<Role>) claims.get("role"));
            return user; //devuelve el usuario de forma decodificafa
        } catch (Exception e) {
            // En caso de que el token sea inválido o haya expirado
            return null;
        }
    }
}