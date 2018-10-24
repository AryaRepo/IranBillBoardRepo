package city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SliderModel
{

    @SerializedName("imagename")
    @Expose
    public String ImageName;

    @SerializedName("slidertitle1")
    @Expose
    public String SliderTitle1;

    @SerializedName("slidertitle2")
    @Expose
    public String SliderTitle2;
    @SerializedName("slidertitle3")
    @Expose
    public String SliderTitle3;
    @SerializedName("slidertitle4")
    @Expose
    public String SliderTitle4;
}
