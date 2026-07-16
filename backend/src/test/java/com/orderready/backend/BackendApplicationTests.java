package com.orderready.backend;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

class BackendApplicationTests {

    @Test
    void generatePasswordHash() {
        System.out.println(BCrypt.hashpw("satis123", BCrypt.gensalt()));
    }
}
