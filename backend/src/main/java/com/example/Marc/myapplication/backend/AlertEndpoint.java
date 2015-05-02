/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.example.Marc.myapplication.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.io.IOException;
import java.lang.String;import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.Marc.myapplication.backend.OfyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(name = "submitAlert", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.myapplication.Marc.example.com", ownerName = "backend.myapplication.Marc.example.com", packagePath = ""))
public class AlertEndpoint {

    private static final String API_KEY = System.getProperty("gcm.api.key");
    private static final Logger log = Logger.getLogger(AlertEndpoint.class.getName());


    @ApiMethod(name = "addAlert")
    public void addAlert(@Named("alertName") String alert,@Named("latitude") Double latitude,@Named("longitude") Double longitude, @Named("regId") String regId,@Named("description") String description) {
        if (findRecord(alert) != null) {
            log.info("Device " + alert + " already registered, skipping register");
            return;
        }
        AlertRecord record = new AlertRecord();
        record.setAlertId(alert);
        record.setLatLng(latitude, longitude);
        record.setRegId(regId);
        record.setDescription(description);
        ofy().save().entity(record).now();
        try {
            sendPushNotificationOnAlertUpdated("Alert added");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @ApiMethod(name = "deleteAlert")
    public void deleteAlert(@Named("alertName") String alert) {
        if (findRecord(alert) != null) {
            ofy().delete().entity(findRecord(alert));
            try {
                sendPushNotificationOnAlertUpdated("Alert deleted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            log.info("Alert doesn't exist");
            return;
        }
    }
    /**
     * Return a collection of registered devices
     *
     * @param count The number of devices to list
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listAlerts")
    public CollectionResponse<AlertRecord> listDevices(@Named("count") int count) {
        List<AlertRecord> records = ofy().load().type(AlertRecord.class).limit(count).list();
        return CollectionResponse.<AlertRecord>builder().setItems(records).build();
    }

    public void sendPushNotificationOnAlertUpdated(@Named("message") String message) throws IOException {
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).list();
        for (RegistrationRecord record : records) {
            Result result = sender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                } else {
                    log.warning("Error when sending message : " + error);
                }
            }
        }
    }
    private AlertRecord findRecord(String alertId) {
        return ofy().load().type(AlertRecord.class).filter("alertId", alertId).first().now();
    }

}
