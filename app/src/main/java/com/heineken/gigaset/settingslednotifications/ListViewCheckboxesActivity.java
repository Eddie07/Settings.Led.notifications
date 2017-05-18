package com.heineken.gigaset.settingslednotifications;

/**
 * Created by Dima on 25-2-2017.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.pm.PackageInfo;

public class ListViewCheckboxesActivity extends Activity {
    Switch sw1;

    MyCustomAdapter dataAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sw1  = (Switch)  findViewById(R.id.switch1);
        String line=readSystemProperty("persist.sys.ledlight");
       // Log.i("Prop is", line);
        sw1.setChecked(false); ;
        if (line.equals("blue")) sw1.setChecked(true);




        //Generate list View from ArrayList
        displayListView();

        checkButtonClick();

        }

    public static String readSystemProperty(String name) {
        InputStreamReader in = null;
        BufferedReader reader = null;
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"/system/bin/getprop", name});
            in = new InputStreamReader(proc.getInputStream());
            reader = new BufferedReader(in);
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
    private void displayListView() {
        ArrayList<Country> countryList = new ArrayList<Country>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);


        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);

            if ((isSystemPackage(p) == false)) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                String pakname = p.packageName.toString();
           //     Log.i("name:", appName);
           //     Log.i("Pakname", pakname);


                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                Country country = new Country(icon, appName, pakname, false);
                countryList.add(country);
                //    res.add(new AppList(appName, icon));
            }
        }
                try {
                    InputStream inputStream = new FileInputStream("system/etc/LedApp.list");
                    if ( inputStream != null ) {

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString;

                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            for (int i=1; i<countryList.size();++i) {
                               // Log.i ("test","test");

                                if (receiveString.equals(countryList.get(i).getPakname()))
                                {
                                    countryList.get(i).setSelected(true);
                                }
                            }

                        }

                        inputStream.close();

                    }
                }
                catch (FileNotFoundException e) {
                    //       Log.e("File not found: ","");
                } catch (IOException e) {
                    //       Log.e("Can not read file: " ,"");
                }




        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this, R.layout.app_info, countryList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


 /*       listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                       int position, long id) {
                // When clicked, show a toast with the TextView text
                Country country = (Country) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + country.getName(),
                        Toast.LENGTH_LONG).show();
                country.setSelected(true);
                }
            });
            */

        }
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;

    }

   private class MyCustomAdapter extends ArrayAdapter<Country> {

                private ArrayList<Country> countryList;

                public MyCustomAdapter(Context context, int textViewResourceId,
                                         ArrayList<Country> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Country>();
            this.countryList.addAll(countryList);
            }

        private class ViewHolder {
            ImageView icon;
            CheckBox name;
            }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.d("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.app_info, null);

                holder = new ViewHolder();
               holder.icon= (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

             holder.name.setOnClickListener( new View.OnClickListener() {
                  public void onClick(View v) {
                  CheckBox cb = (CheckBox) v ;

               Country country = (Country) cb.getTag();

                      country.setSelected(cb.isChecked());
                    }
                       });
                }
            else {
                holder = (ViewHolder) convertView.getTag();
                }

            Country country = countryList.get(position);
     holder.icon.setImageDrawable(country.getIcon());
            holder.name.setText(country.getName());
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);
            return convertView;

            }

                }

            private void checkButtonClick() {

                sw1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String prop;
                        if (sw1.isChecked()) {
                            prop = "blue";
                        } else prop="green";

                        Process suProcess;
                        DataOutputStream os;
                        try{

                            suProcess = Runtime.getRuntime().exec("su");

                            os= new DataOutputStream(suProcess.getOutputStream());
                            os.writeBytes("setprop persist.sys.ledlight "+prop+"\n");
                            os.flush();
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.RebootReq),
                                    Toast.LENGTH_SHORT).show();
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    });


                   Button myButton = (Button) findViewById(R.id.findSelected);
                myButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Process suProcess;
                DataOutputStream os;
                try{
                    //Get Root
                    suProcess = Runtime.getRuntime().exec("su");
                    // suProcess = Runtime.getRuntime().exec("mount -o remount,rw /system");
                    os= new DataOutputStream(suProcess.getOutputStream());

                    //Remount writable FS within the root process
                    os.writeBytes("mount -o rw,remount /system\n");
                    Log.i("Mounted","system");


                    os.flush();

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");


                ArrayList<Country> countryList = dataAdapter.countryList;

                try {
                    FileOutputStream fileout=new FileOutputStream("sdcard/test.txt");

                    OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                    for(int i=0;i<countryList.size();i++){
                        Country country = countryList.get(i);
                        if(country.isSelected()){
                            responseText.append("\n" + country.getName());
                            outputWriter.write(country.getPakname()+System.getProperty("line.separator"));
                        }
                        if (i==100) {
                            i = countryList.size();
                            Toast.makeText(getBaseContext(), "LIMITED TO 100 APPS!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    outputWriter.close();

                    //display file saved message
                    Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.RebootReq),
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }




                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
                    os.writeBytes("rm system/etc/LedApp.list\n");
                    os.flush();
                    os.writeBytes("cp sdcard/test.txt system/etc/LedApp.list\n");

                    os.flush();
                    os.writeBytes("chmod 777 system/etc/LedApp.list\n");

                    os.flush();



                    os.writeBytes("exit\n");
                    os.flush();


                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }

                }
            });

        }

}