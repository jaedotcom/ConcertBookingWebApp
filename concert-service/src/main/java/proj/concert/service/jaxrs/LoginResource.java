package proj.concert.service.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import proj.concert.service.domain.User;
import proj.concert.service.dao.UserDao;

import java.util.UUID;


//**Token-based authentication**. The web service specification sometimes calls for an authenticated user.
// Authentication should be implemented using *token-based authentication*,
// which is where the client exchanges credentials (username and password) for a token.
// Once authenticated, the client sends the token with each subsequent request to the web service.
// For any requests requiring authentication, the Web service checks for the presence of the token.
// If the token's missing, the request fails; if the token doesn't identify a user (based upon accessing
// storage to lookup any user to whom the token is associated), the request fails. Where the token is
// bound to a particular user, the user is then authenticated, and the web service may then
// determine whether the user is authorised to make the request.

@Path("/login")
public class LoginResource {
    private final UserDao userDao = new UserDao();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(User credentials) {
        try {
            authenticate(credentials.getUsername(), credentials.getPassword());

            String token = issueToken(credentials.getUsername());

            // Return 200 status code
            return Response.status(Response.Status.OK).entity(token).build();

        } catch (Exception e) {
            // Return 401 = unauthorized requests
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    // throw exception or 401? Idk.
    private void authenticate(String username, String password) throws Exception {
        // using Dao here.
        User user = userDao.findUserByUsername(username);
        if(user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid credentials");
        }
    }

    private String issueToken(String username) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be able to be linked to the user
        return username + ":" + UUID.randomUUID().toString();
    }
}