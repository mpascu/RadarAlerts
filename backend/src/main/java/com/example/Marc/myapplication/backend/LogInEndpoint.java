package com.example.Marc.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import static com.example.Marc.myapplication.backend.OfyService.ofy;

import java.util.logging.Logger;

/**
 * Created by Marc on 02/05/2015.
 */
@Api(name = "loginApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.myapplication.Marc.example.com", ownerName = "backend.myapplication.Marc.example.com", packagePath = ""))
public class LogInEndpoint {
    private static final Logger log = Logger.getLogger(LogInEndpoint.class.getName());

    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "login")
    public Result login(@Named("username") String username,
                        @Named("password") String password) {
        //UserRecord record = new UserRecord();
        //record.setUser(username);
        //record.setPassword(password);
        UserRecord user = ofy().load().type(UserRecord.class).filter("user", username).first().now();
        if (user == null) {
            return new Result(false, "L'usuari no existeix");
        } else if (user.getPassword().equals(password)) {
            return new Result(true, "OK");
        } else {
            return new Result(false, "Contrasenya incorrecta");
        }
    }

    @ApiMethod(name = "registerUser")
    public Result registerUser(@Named("username") String username,
                           @Named("password") String password
                           ) {
        UserRecord user = ofy().load().type(UserRecord.class).filter("user", username).first().now();
        if (user == null) {
            UserRecord record = new UserRecord();
            record.setUser(username);
            record.setPassword(password);
            ofy().save().entity(record).now();
            return new Result(true, "Usuari registrat correctament");
        } else {
            return new Result(false, "Ja existeix un usuari amb aquest nom");
        }
    }

}