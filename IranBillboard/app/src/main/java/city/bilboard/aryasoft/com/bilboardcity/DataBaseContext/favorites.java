package city.bilboard.aryasoft.com.bilboardcity.DataBaseContext;

import com.orm.SugarRecord;

public class favorites extends SugarRecord
{
    public favorites()
    {
    }

    public favorites(int BoardID, String BoardName,String BoardCategory,String BoardPrice)
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
