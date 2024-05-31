package org.dindier.oicraft.assets.constant;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public record ConfigConstants() {

    /****************
     * User parameters
     *****************/

    // The experience for different level users
    public static final int INTERMEDIATE_MIN_EXP = 100;
    public static final int ADVANCED_MIN_EXP = 200;
    public static final int EXPERT_MIN_EXP = 300;
    // Email valid time for authentication
    public final static long EMAIL_VALID_TIME = TimeUnit.MINUTES.toMillis(5);

    /*****************
     * Problem parameters
     *****************/

    // Thread pool parameters for testing code
    public static final int TEST_POOL_SIZE = 16;
    public static final int WAITING_QUEUE_SIZE = 10000;
    // The temp folder to storage the temp file in testing
    public static final String TEST_FOLDER = "/temp";
    // View at the website
    public static final int PROBLEMS_PER_PAGE = 20;
    public static final int SUBMISSIONS_PER_PAGE = 20;

    /******************
     * Server Parameters
     ******************/

    public static final String SERVER_RESOURCE_URL;
    public static final String SERVER_RESOURCE_FOLDER;

    static {
        // Read the configuration from the environment
        String serverResourceUrl = System.getenv("SERVER_RESOURCE_URL");
        SERVER_RESOURCE_URL = Objects.requireNonNullElse(serverResourceUrl, "http://123.56.220.173:8000");
        String serverResourceFolder = System.getenv("SERVER_RESOURCE_FOLDER");
        SERVER_RESOURCE_FOLDER = Objects.requireNonNullElse(serverResourceFolder, "/mnt/nas");
    }
}
