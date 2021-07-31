package com.changgou.file;

import com.changgou.utils.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: lee
 * @date: 2021-07-31
 **/
@RestController
public class UploadController {
    
    @Value("${pic.url}")
    private String url;
    
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        if (! file.isEmpty()) {
            // 1. 获取文件字节数组
            byte[] bytes = file.getBytes();
            // 2. 获取文件名
            String fileName = file.getOriginalFilename();
            // 3. 获取文件扩展名
            String extName = StringUtils.getFilenameExtension(fileName);
            // 4. 调用工具类的方法上传文件
            String[] upload = FastDFSClient.upload(new FastDFSFile(fileName, bytes, extName));

            String realPath = url + "/" + upload[0] + "/" + upload[1];

            return Result.ok(realPath);
        }

        return null;
    }
}
