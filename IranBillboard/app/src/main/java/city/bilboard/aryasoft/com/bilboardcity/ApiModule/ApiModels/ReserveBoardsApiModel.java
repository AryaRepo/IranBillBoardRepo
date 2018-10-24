package city.bilboard.aryasoft.com.bilboardcity.ApiModule.ApiModels;

import java.util.ArrayList;

public class ReserveBoardsApiModel
{
    private int UserId;
    private ArrayList<Integer>BoardsId;
    public void AddBoardId(int BoardId)
    {
        BoardsId.add(BoardId);
    }

    public void SetUserId(int userId)
    {
        UserId = userId;
    }

    public ReserveBoardsApiModel()
    {
        this.BoardsId=new ArrayList<>();
    }


}
