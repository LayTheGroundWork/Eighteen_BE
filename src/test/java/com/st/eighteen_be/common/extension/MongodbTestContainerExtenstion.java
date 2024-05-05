package com.st.eighteen_be.common.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * packageName    : com.st.eighteen_be.common.extension
 * fileName       : MongodbTestContainerExtenstion
 * author         : ipeac
 * date           : 24. 5. 5.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 5.        ipeac       최초 생성
 */
public class MongodbTestContainerExtenstion implements BeforeAllCallback {
    private static final DockerImageName MONGO_IMAGE = DockerImageName.parse("mongo:7.0.8");
    private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(MONGO_IMAGE);
    
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (MONGO_DB_CONTAINER.isRunning()) {
            return;
        }
        
        try {
            MONGO_DB_CONTAINER.start();
            System.setProperty("spring.data.mongodb.uri", MONGO_DB_CONTAINER.getReplicaSetUrl());
            
            //execute mongo init script
            MONGO_DB_CONTAINER.copyFileToContainer(MountableFile.forClasspathResource("mongo-init.js"), "/docker-entrypoint-initdb.d/init.js");
            MONGO_DB_CONTAINER.execInContainer("mongo", "/docker-entrypoint-initdb.d/init.js");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}