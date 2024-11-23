package com.daicy.minitomcat;

import com.daicy.minitomcat.core.StandardContext;

import java.net.URL;
import java.nio.file.*;

import static com.daicy.minitomcat.HttpServer.CONF_PATH;

public class HotDeployment {

//    private static final String APP_BASE_PATH = "/path/to/apps";  // Web 应用目录

    public void startDeploymentMonitor() {
        try {


            URL url = getClass().getResource(CONF_PATH);
            Path path = Paths.get(url.getPath());
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // 注册监控事件：创建、修改和删除
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            System.out.println("Monitoring directory for changes: " + path.toString());

            // 监控目录变化
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();  // 等待文件变化事件
                } catch (InterruptedException e) {
                    System.out.println("Monitoring interrupted");
                    return;
                }

                // 处理监控到的事件
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path filePath = (Path) event.context();
                    System.out.println("Event detected: " + kind + " on file: " + filePath);

                    // 根据事件类型进行相应处理
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        // 新应用被添加，加载该应用
                        deployApplication(filePath);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        // 修改了应用，重新加载或更新
                        reloadApplication(filePath);
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        // 应用被删除，卸载应用
                        undeployApplication(filePath);
                    }
                }

                // 重置 key 以继续监控
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 部署新应用
    private void deployApplication(Path filePath) {
        System.out.println("Deploying new application: " + filePath);
        // 实际应用加载逻辑
        // 例如加载 Servlet、web.xml 配置等
    }

    // 重新加载应用
    private void reloadApplication(Path filePath) {
        System.out.println("Reloading application: " + filePath);
        // 重新加载应用的 Servlet 和配置
        try {
            HttpServer.context = new StandardContext("/conf/web.xml");
            HttpServer.context.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 卸载应用
    private void undeployApplication(Path filePath) {
        System.out.println("Undeploying application: " + filePath);
        // 卸载应用，释放资源
    }
}

