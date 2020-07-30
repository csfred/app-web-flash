package cn.enilu.flash.api.controller;

import cn.enilu.flash.bean.entity.system.FileInfo;
import cn.enilu.flash.bean.enumeration.Permission;
import cn.enilu.flash.bean.vo.front.Results;
import cn.enilu.flash.service.system.FileService;
import cn.enilu.flash.utils.CryptUtil;
import cn.enilu.flash.utils.HttpUtil;
import cn.enilu.flash.utils.Maps;
import cn.enilu.flash.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author cs
 * @date 2020/07/28
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {
    @Resource
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param multipartFile 分片文件
     * @return Object
     */
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions(value = {Permission.FILE_UPLOAD})
    public Object upload(@RequestPart("file") MultipartFile multipartFile) {

        try {
            FileInfo fileInfo = fileService.upload(multipartFile);
            return Results.success(fileInfo);
        } catch (Exception e) {
            log.error("上传文件异常", e);
            return Results.failure("上传文件失败");
        }
    }

    /**
     * 下载文件
     *
     * @param idFile 文件ID
     * @param fileName 文件名
     */
    @RequestMapping(value = "download", method = RequestMethod.GET)
    public void download(@RequestParam("idFile") Long idFile,
                         @RequestParam(value = "fileName", required = false) String fileName) {
        FileInfo fileInfo = fileService.get(idFile);
        fileName = StringUtil.isEmpty(fileName) ? fileInfo.getOriginalFileName() : fileName;
        HttpServletResponse response = HttpUtil.getResponse();
        response.setContentType("application/x-download");
        try {
            fileName = new String(fileName.getBytes(), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        OutputStream os;
        try {
            File file = new File(fileInfo.getAblatePath());
            os = response.getOutputStream();
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer);
                i = bis.read(buffer);
            }

        } catch (Exception e) {
            log.error("download error", e);
        } finally {
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                log.error("close inputstream error", e);
            }
        }

    }

    /**
     * 获取base64图片数据
     *
     * @param idFile
     * @return
     */
    @RequestMapping(value = "getImgBase64", method = RequestMethod.GET)
    public Object getImgBase64(@RequestParam("idFile") Long idFile) {

        FileInfo fileInfo = fileService.get(idFile);
        FileInputStream fis = null;
        try {
            File file = new File(fileInfo.getAblatePath());
            byte[] bytes = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytes);
            String base64 = CryptUtil.encodeBASE64(bytes);
            return Results.success(Maps.newHashMap("imgContent", base64));
        } catch (Exception e) {
            log.error("get img error", e);
            return Results.failure("获取图片异常");
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                log.error("close getImgBase64 error", e);
            }
        }

    }

    /**
     * 获取图片流
     *
     * @param response 响应体
     * @param idFile 文件ID
     */
    @RequestMapping(value = "getImgStream", method = RequestMethod.GET)
    public void getImgStream(HttpServletResponse response,
                             @RequestParam("idFile") Long idFile) {
        FileInfo fileInfo = fileService.get(idFile);
        FileInputStream fis = null;
        response.setContentType("image/" + fileInfo.getRealFileName().split("\\.")[1]);
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(fileInfo.getAblatePath());
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("getImgStream error", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("close getImgStream error", e);
                }
            }
        }
    }
}
