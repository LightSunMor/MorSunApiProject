package com.morsun.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.morsun.springbootinit.common.BaseResponse;
import com.morsun.springbootinit.common.ErrorCode;
import com.morsun.springbootinit.common.ResultUtils;
import com.morsun.springbootinit.constant.CommonConstant;
import com.morsun.springbootinit.constant.FileConstant;
import com.morsun.springbootinit.exception.BusinessException;
import com.morsun.springbootinit.manager.CosManager;
import com.morsun.springbootinit.model.dto.file.UploadFileRequest;
import com.morsun.springbootinit.model.entity.User;
import com.morsun.springbootinit.model.enums.FileUploadBizEnum;
import com.morsun.springbootinit.model.vo.ImgVo;
import com.morsun.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * 文件接口
 *
 * @author morsun
 * @from 知识星球
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    private BaseResponse<ImgVo> uploadError(ImgVo imageVo, MultipartFile multipartFile, String message) {
        imageVo.setName(multipartFile.getOriginalFilename());
        imageVo.setUid(RandomStringUtils.randomAlphanumeric(8));
        imageVo.setStatus(CommonConstant.ERROR);
        return ResultUtils.error(imageVo, ErrorCode.OPERATION_ERROR, message);
    }

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<ImgVo> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        // 返回ImgVo
        ImgVo imgVo = new ImgVo();

        if (fileUploadBizEnum == null) {
            return uploadError(imgVo,multipartFile,"上传失败，请重试");
        }
        // 检验上传的文件是否符合要求
        String validFile = validFile(multipartFile, fileUploadBizEnum);
        if (!"ok".equals(validFile))
        {
            return uploadError(imgVo,multipartFile,validFile);
        }
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        // 新文件名，在原来基础上加上 uuid-
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // COS 上传文件
                // 1.创建文件及文件夹
            file = File.createTempFile(filepath, null);
                // 2.上传文件
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址 （图片线上地址）
            imgVo.setName(multipartFile.getOriginalFilename());
            imgVo.setUid(RandomStringUtils.randomAlphanumeric(8));
            imgVo.setStatus(CommonConstant.SUCCESS);
            imgVo.setUrl(FileConstant.COS_HOST + filepath);
            log.info("图片在线地址："+FileConstant.COS_HOST + filepath);
            return ResultUtils.success(imgVo);
        } catch (Exception e) {
            log.error("文件上传错误，请查看, filepath = " + filepath, e);
            return uploadError(imgVo, multipartFile, "上传失败,情重试"+e.getMessage());
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("文件删除错误，请处理, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private String validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 2*1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                return "文件大小不能超过 2M";
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                return "文件类型错误";
            }
        }
        return "ok";
    }
}
