package com.heineken.gigaset.settingslednotifications;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.widget.ImageView;

/**
 * Created by Dima on 25-2-2017.
 */

public class Country {



    String name = null;
    String pakname = null;
    boolean selected = false;
    Drawable icon=null;

    //public Country(Drawable icon, String name, boolean selected) {
    public Country(Drawable icon, String name, String pakname, boolean selected) {
        super();
      this.icon=icon;
         this.name = name;
        this.pakname = pakname;
        this.selected = selected;
        }


  public Drawable getIcon() {

       return icon;
    }
  //  public Drawable setIcon(Drawable icon) {

  //      this.icon=icon;
 //       return null;
 //   }
    public String getName() {

        return name;
        }
  //  public void setName(String name) {

  //      this.name = name;
  //      }
    public String getPakname() {

        return pakname;
    }
    public void setPakname(String pakname) {

        this.pakname = pakname;
    }
    public boolean isSelected() {

                return selected;
        }
    public void setSelected(boolean selected) {

        this.selected = selected;
        }

}