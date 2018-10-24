package city.bilboard.aryasoft.com.bilboardcity.Models;

public class ProductBoardModel
{
    public ProductBoardModel(Integer BoardsID, String BoardsTitle, Integer CategoryBoardID, String ImageName, Integer AveragePoint, Integer Price, Integer SituationID,String CategoryBoardTitle)
    {
        this.BoardsID=BoardsID;
        this.BoardsTitle=BoardsTitle;
        this.CategoryBoardID=CategoryBoardID;
        this.ImageName=ImageName;
        this.AveragePoint=AveragePoint;
        this.Price=Price;
        this.SituationID=SituationID;
        this.CategoryBoardTitle=CategoryBoardTitle;
    }
    public ProductBoardModel(Integer BoardsID, String BoardsTitle, Integer CategoryBoardID, String ImageName, Integer AveragePoint, Integer Price, String SituationTitle,String CategoryBoardTitle)
    {
        this.BoardsID=BoardsID;
        this.BoardsTitle=BoardsTitle;
        this.CategoryBoardID=CategoryBoardID;
        this.ImageName=ImageName;
        this.AveragePoint=AveragePoint;
        this.Price=Price;
        this.SituationTitle=SituationTitle;
        this.CategoryBoardTitle=CategoryBoardTitle;
    }
    public Integer BoardsID;
    public String  BoardsTitle;
    public Integer  CategoryBoardID;
    public String  ImageName;
    public Integer AveragePoint;
    public Integer Price;
    public Integer SituationID=-1;
    public String SituationTitle;
    public String  CategoryBoardTitle;


}
