// Presumed path: com/example/myapplication/SliderData.java
package com.example.myapplication;

public class SliderData {

    // Fields
    private Object imageSource; // Can be Integer (R.drawable.id) or String (URL or URI string)
    private String targetUrl;   // URL to open on click
    private String wzrkId;      // CleverTap unit ID for click tracking

    // Constructor
    // This constructor allows you to pass local drawables (as Integer)
    // or image URLs/URIs (as String) along with click data.
    public SliderData(Object imageSource, String targetUrl, String wzrkId) {
        this.imageSource = imageSource;
        this.targetUrl = targetUrl;
        this.wzrkId = wzrkId;
    }

    // You might have or want overloaded constructors if you prefer, for example:
    // If you are SURE your local drawables will always be passed as R.drawable.id
    // and network ones as Strings, and you don't want to rely on the URI conversion
    // for local ones before passing to this constructor.
    // However, the single constructor with Object is often more flexible with the adapter logic we have.

    // public SliderData(String imageStringUrlOrUri, String targetUrl, String wzrkId) {
    //     this.imageSource = imageStringUrlOrUri;
    //     this.targetUrl = targetUrl;
    //     this.wzrkId = wzrkId;
    // }

    // public SliderData(int imageResourceId, String targetUrl, String wzrkId) {
    //    this.imageSource = imageResourceId;
    //    this.targetUrl = targetUrl;
    //    this.wzrkId = wzrkId;
    // }


    // Getter methods
    public Object getImageSource() {
        return imageSource;
    }

    // Setter methods (optional, if you need to modify data after creation)
    public void setImageSource(Object imageSource) {
        this.imageSource = imageSource;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getWzrkId() {
        return wzrkId;
    }

    public void setWzrkId(String wzrkId) {
        this.wzrkId = wzrkId;
    }
}
