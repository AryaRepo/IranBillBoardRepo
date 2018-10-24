package city.bilboard.aryasoft.com.bilboardcity.DataBaseContext;
import com.orm.SugarRecord;
public class basket extends SugarRecord
{
    public basket()
    {
    }

    public basket(int BoardID, String BoardName,String BoardCategory,String BoardPrice)
    {
        this.boardid = BoardID;
        this.boardname = BoardName;
        this.boardcategory = BoardCategory;
        this.boardprice = BoardPrice;
    }

    public int boardid;
    public String boardname;
    public String boardcategory;
    public String boardprice;
}
