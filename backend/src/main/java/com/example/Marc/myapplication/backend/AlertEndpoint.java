/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.example.Marc.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

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

    private static final Logger log = Logger.getLogger(AlertEndpoint.class.getName());


    @ApiMethod(name = "addAlert")
    public void addAlert(@Named("alertName") String alert,@Named("latitude") Double latitude,@Named("longitude") Double longitude) {
        if (findRecord(alert) != null) {
            log.info("Device " + alert + " already registered, skipping register");
            return;
        }
        AlertRecord record = new AlertRecord();
        record.setAlertId(alert);
        record.setLatLng(latitude, longitude);
        ofy().save().entity(record).now();
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

    private AlertRecord findRecord(String alertId) {
        return ofy().load().type(AlertRecord.class).filter("alertId", alertId).first().now();
    }

}
