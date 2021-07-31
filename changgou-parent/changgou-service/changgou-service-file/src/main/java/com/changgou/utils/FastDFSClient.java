package com.changgou.utils;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: lee
 * @date: 2021-07-31
 * 文件操作工具类
 **/
public class FastDFSClient {
    static {
        ClassPathResource classPathResource = new ClassPathResource("fdfs_client.conf");
        String path = classPathResource.getPath();
        try {
            ClientGlobal.init(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
    /**
     * 文件上传
     * @param file
     * @return
     * @throws Exception
     */
    public static String[] upload(FastDFSFile file) throws Exception {
        // 3. 创建 trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();
        // 4. 获取 trackerServer 对象
        TrackerServer trackerServer = trackerClient.getConnection();
        // 5. 创建 storageClient 对象
        StorageClient storageClient = new StorageClient(trackerServer,null);

        NameValuePair[] meta_list = new NameValuePair[]{
                new NameValuePair(file.getName()),
                new NameValuePair(file.getAuthor())
        };

        return storageClient.upload_file(file.getContent(),file.getExt(),meta_list);
    }

    /**
     * 下载文件
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static byte[] downFile(String groupName, String remoteFileName) throws Exception {
        // 3. 创建 trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();
        // 4. 获取 trackerServer 对象
        TrackerServer trackerServer = trackerClient.getConnection();
        // 5. 创建 storageClient 对象
        StorageClient storageClient = new StorageClient(trackerServer,null);

        return storageClient.download_file(groupName, remoteFileName);
    }

    /**
     * 删除文件
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static boolean deleteFile(String groupName, String remoteFileName) throws Exception {
        // 3. 创建 trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();
        // 4. 获取 trackerServer 对象
        TrackerServer trackerServer = trackerClient.getConnection();
        // 5. 创建 storageClient 对象
        StorageClient storageClient = new StorageClient(trackerServer,null);

        int i = storageClient.delete_file(groupName, remoteFileName);

        return i == 0;
    }

    /**
     * 获取文件信息
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) throws Exception {
        // 3. 创建 trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();
        // 4. 获取 trackerServer 对象
        TrackerServer trackerServer = trackerClient.getConnection();
        // 5. 创建 storageClient 对象
        StorageClient storageClient = new StorageClient(trackerServer,null);

        return storageClient.get_file_info(groupName, remoteFileName);
    }
}
