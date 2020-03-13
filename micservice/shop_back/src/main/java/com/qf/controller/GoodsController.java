package com.qf.controller;

import com.qf.entity.Goods;
import com.qf.entity.GoodsSecondKill;
import com.qf.entity.ResultData;
import com.qf.feign.IGoodsFeign;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    //图片上传路径
    private String uploadPath = "F:/work/images";

    @Autowired
    private IGoodsFeign iGoodsFeign;
   /* //准备一个本地磁盘的路径
    private String localpath ="F:/mall1909Picture/images";
    @Reference
    private IGoodsService iGoodsService;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

*/

    /**
     * 查询商品列表
     *
     * @return
     */
    @RequestMapping("/getGoodsList")
    public String getGoodsList(ModelMap map) {
        List<Goods> goodsList = iGoodsFeign.goodsList();
        map.addAttribute("goodsList", goodsList);
        return "goodslist";
    }
    /*

     *//**
     * 测试ajax
     * @return
     *//*
    @RequestMapping("/ajax")
    @ResponseBody
    public ResultData<String> ajax(){
        System.out.println(1/0);
        return new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setMsg("操作成功");
    }

    */

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @RequestMapping("/shangchuan")
    @ResponseBody
    public ResultData<String> shangchuan(MultipartFile file) {

   /*     //1.输出流必须写到文件名，所以先得到一个文件名
        String fileName = UUID.randomUUID().toString();
        //2.上传的真实路径
        String ShangChuanRealpath = localpath + "/" + fileName;

        //3.上传到fastdfs
        String nginxPath=null;
        try {
            StorePath resultPath = fastFileStorageClient.uploadImageAndCrtThumbImage(
                    file.getInputStream(),
                    file.getSize(),
                    "JPG",
                    null
            );
            System.out.println("上传结果：" + resultPath.getFullPath());
            nginxPath = resultPath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

     try(
                //从file得到一个输入流
                InputStream is = file.getInputStream();
                //构造一个输出流
                OutputStream out = new FileOutputStream(ShangChuanRealpath);
                ) {
            IOUtils.copy(is,out);
        }catch (Exception e) {
            e.printStackTrace();
        }*/

        //1.输出流必须写到文件名，所以先得到一个文件名,例如：3664364673746
        String fileName = UUID.randomUUID().toString();
        //2.上传的真实路径 例如：F:/work/images/3664364673746
        String ShangChuanRealpath = uploadPath + "/" + fileName;
        //3.复制拷贝图片，从file---》F:/work/images/3664364673746
        try (
                //从file得到一个输入流
                InputStream is = file.getInputStream();
                //构造一个输出流
                OutputStream out = new FileOutputStream(ShangChuanRealpath);
        ) {
            //复制拷贝图片
            IOUtils.copy(is, out);
            return new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setData(ShangChuanRealpath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResultData<String>().setCode(ResultData.ResultCodeList.ERROR);
    }

    /**
     * ajax上传图片的成功回调函数给img标签设置地址，img标签发送请求，
     * 方法将图片信息响应回img
     *
     * @param imgPath
     * @param response
     */
    @RequestMapping("queryImageByServer")
    public void queryImageByServer(String imgPath, HttpServletResponse response) {
        try (
                InputStream in = new FileInputStream(imgPath);
                ServletOutputStream out = response.getOutputStream();
        ) {
            IOUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    @RequestMapping("/addGood")
    public String addGood(Goods goods, GoodsSecondKill goodsSecondKill) {
        goods.setGoodsKill(goodsSecondKill);
        System.out.println("接收到form的数据" + goods);
        iGoodsFeign.addGoods(goods);
        //重定向到查询页面
        return "redirect:http://localhost:8081/back/goods/queryGoodsListAfterAddGood";
    }


    /**
     * 添加成功后查询图片回到列表页
     *
     * @param map
     * @return
     */
    @RequestMapping("/queryGoodsListAfterAddGood")
    public String queryGoodsListAfterAddGood(ModelMap map) {
        List<Goods> goodsList = iGoodsFeign.goodsList();
        System.out.println("后台服务接收到商品服务信息:" + goodsList);
        map.addAttribute("goodsList", goodsList);
        return "goodslist";
    }
}
