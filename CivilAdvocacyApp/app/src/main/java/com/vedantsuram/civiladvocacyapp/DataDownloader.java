package com.vedantsuram.civiladvocacyapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class DataDownloader implements Runnable {

    MainActivity mainActivity;
    String URL_KEY_API = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyB6OeNElEgzphPTJwJXF2bwO9Ov5IipEt8&address=";
    String string;


    public DataDownloader(MainActivity mainActivity, String string) {
        this.mainActivity = mainActivity;
        this.string = string;
    }

    @Override
    public void run() {
        StringBuilder stringBuilder = new StringBuilder();
        String urlToUse = URL_KEY_API + string;
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String Line;
            while((Line = bufferedReader.readLine()) != null) {
                stringBuilder.append(Line).append('\n');
            }

        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Object> returnList = parseJSON(stringBuilder.toString());
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.SettingList(returnList);
            }
        });
    }

    private ArrayList<Object> parseJSON(String s) {
        ArrayList<Object> resultObjList = new ArrayList<>();
        String Address;
        ArrayList<Official> officialList = new ArrayList<>();

        try {
            JSONObject JSONobj = new JSONObject(s);
            JSONObject normalizedInput = JSONobj.getJSONObject("normalizedInput");

            Address = normalizedInput.getString("line1") + " " + normalizedInput.getString("city") + ", " + normalizedInput.getString("state")
                    +" "+ normalizedInput.getString("zip");

            JSONArray offices = JSONobj.getJSONArray("offices");
            JSONArray officials = JSONobj.getJSONArray("officials");

            for (int i = 0; i < offices.length(); i++) {
                JSONObject JSONobjOffices = (JSONObject) offices.get(i);

                String officeName = "";
                if (JSONobjOffices.has("name")) {
                    officeName = JSONobjOffices.getString("name");
                }

                JSONArray Indices = JSONobjOffices.getJSONArray("officialIndices");

                for (int j = 0; j < Indices.length(); j++) {

                    JSONObject jsonObjectOfficials = (JSONObject) officials.get((int) Indices.get(j));

                    String name = "",address = "" ,party = "" ,phones = "", urls = "", email = "", photoUrl = null;
                    Map<String, String> channels = new HashMap<String, String>();

                    if (jsonObjectOfficials.has("name")) {
                        name = jsonObjectOfficials.getString("name");
                    }

                    if (jsonObjectOfficials.has("address")) {
                        JSONObject addressJObj = (JSONObject) jsonObjectOfficials.getJSONArray("address").get(0);

                        String line1 = "", line2 = "", city = "", state = "", zip = "";
                        if (addressJObj.has("line1"))
                            line1 = addressJObj.getString("line1");
                        if (addressJObj.has("line2"))
                            line2 = addressJObj.getString("line2");
                        if (addressJObj.has("city"))
                            city = addressJObj.getString("city");
                        if (addressJObj.has("state"))
                            state = addressJObj.getString("state");
                        if (addressJObj.has("zip"))
                            zip = addressJObj.getString("zip");

                        address = line1 + " " + line2 + " " + city + ", " + state + " " + zip;
                    }

                    if (jsonObjectOfficials.has("party")) {
                        party = jsonObjectOfficials.getString("party");
                    }

                    if (jsonObjectOfficials.has("phones")) {
                        phones = (String) jsonObjectOfficials.getJSONArray("phones").get(0);
                    }

                    if (jsonObjectOfficials.has("emails")) {
                        email = (String) jsonObjectOfficials.getJSONArray("emails").get(0);
                    }

                    if (jsonObjectOfficials.has("urls")) {
                        urls = (String) jsonObjectOfficials.getJSONArray("urls").get(0);
                    }

                    if (jsonObjectOfficials.has("photoUrl")) {
                        photoUrl = jsonObjectOfficials.getString("photoUrl");
                    }

                    if (jsonObjectOfficials.has("channels")) {
                        JSONArray channelsJArray = jsonObjectOfficials.getJSONArray("channels");

                        for (int k = 0; k < channelsJArray.length(); k++) {
                            try {
                                JSONObject jObjChannel = (JSONObject) channelsJArray.get(k);
                                String type = jObjChannel.getString("type");
                                String id = jObjChannel.getString("id");
                                channels.put(type, id);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        channels = null;
                    }

                    officialList.add(new Official(name, officeName, party, address , phones, email, urls, photoUrl, channels));
                }

            }
            resultObjList.add(Address);
            resultObjList.add(officialList);

            return resultObjList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
