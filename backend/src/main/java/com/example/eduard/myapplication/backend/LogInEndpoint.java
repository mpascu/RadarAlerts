/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.example.eduard.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.eduard.myapplication.backend.OfyService.ofy;

/**
 * An endpoint to send messages to devices registered with the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(name = "login", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.myapplication.eduard.example.com", ownerName = "backend.myapplication.eduard.example.com", packagePath = ""))
public class LogInEndpoint {
    private static final Logger log = Logger.getLogger(LogInEndpoint.class.getName());

    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "login")
    public Result login(@Named("username") String username,
                               @Named("password") String password,
                               @Named("regID") String redId) {
        UserRecord record = new UserRecord();
        record.setUser(username);
        record.setPassword(password);
        UserRecord user = ofy().load().type(UserRecord.class).filter("username", username).first().now();
        if (user == null) {
            return new Result(false, "KO");
        } else if (user.getPassword().equals(password)) {
            return new Result(true, "OK");
        } else {
            return new Result(false, "KO");
        }
    }

    @ApiMethod(name = "register")
    public Result register(@Named("username") String username,
                               @Named("password") String password,
                               @Named("regID") String redId) {
        UserRecord user = ofy().load().type(UserRecord.class).filter("username", username).first().now();
        if (user != null) {
            UserRecord record = new UserRecord();
            record.setUser(username);
            record.setPassword(password);
            ofy().save().entity(record).now();
            return new Result(true, "OK");
        } else {
            return new Result(false, "KO");
        }
    }

}