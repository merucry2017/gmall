package com.merc.gmall.util;

import java.util.List;

public class Page {

    private Integer pageno;
    private Integer pagesize;
    private List datas;
    private Integer totalsize;
    private Integer totalno;

    public Page(Integer pageno, Integer pagesize) {
        if(pageno<=0){
            this.pageno = 1;
        }else {
            this.pageno = pageno;
        }

        if(pagesize<=0){
            this.pagesize = 10;
        }else{
            this.pagesize = pagesize;
        }
    }

    public Integer getPageno() {
        return pageno;
    }

    public void setPageno(Integer pageno) {
        this.pageno = pageno;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public List getDatas() {
        return datas;
    }

    public void setDatas(List datas) {
        this.datas = datas;
    }

    public Integer getTotalsize() {
        return totalsize;
    }

    public void setTotalsize(Integer totalsize) {
        this.totalsize = totalsize;
        /**
         * 例如每页10条数据，总共有98条数据
         * 98/10=9,所以我们需要totalsize/pagesize+1
         * 即 9+1=10页
         */
        this.totalno = (totalsize%pagesize)==0?(totalsize/pagesize):(totalsize/pagesize+1);
    }

    public Integer getTotalno() {
        return totalno;
    }

    public void setTotalno(Integer totalno) {
        this.totalno = totalno;
    }

    public Integer getStartIndex(){
        /**
         * 例：pageno=2，起始数据为（2-1）*pagesize
         * */
        return (this.pageno-1)*pagesize;
    }
}
