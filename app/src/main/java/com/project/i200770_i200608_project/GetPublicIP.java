package com.project.i200770_i200608_project;

import android.os.AsyncTask;
import android.util.Log;

public class GetPublicIP extends AsyncTask<String, String, String> {

    public String publicIP = "";
    @Override
    protected String doInBackground(String... strings) {
        try  {
            java.util.Scanner s = new java.util.Scanner(
                    new java.net.URL(
                            "https://api.ipify.org")
                            .openStream(), "UTF-8")
                    .useDelimiter("\\A");
            publicIP = s.next();
            System.out.println("My current IP address is " + publicIP);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return publicIP;
    }

    @Override
    protected void onPostExecute(String publicIp) {
        super.onPostExecute(publicIp);

        Log.e("PublicIP", publicIp+"");

        //Here 'publicIp' is your desire public IP
    }
    public String getIP() {
        System.out.println("My current IP address 2 is " + publicIP);
        return publicIP;
    }

}
