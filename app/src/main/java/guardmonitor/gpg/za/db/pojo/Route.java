package guardmonitor.gpg.za.db.pojo;

import java.util.Date;

/**
 * Created by Gerhard on 2016/10/11.
 */

public class Route {

    private int _id;
    private String routeName;
    private Date createDate;


    public Route(int id, String name){
        super();
        this._id = id;
        this.routeName = name;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Date getCreateDate() {
        return  this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
