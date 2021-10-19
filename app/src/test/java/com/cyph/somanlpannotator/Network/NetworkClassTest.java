package com.cyph.somanlpannotator.Network;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(RobolectricTestRunner.class)
public class NetworkClassTest {

    @Test
    public void getSomaResponseTest_is() {
        String response = NetworkClass.getSomaResponse("What is the gravity of kuzma");
        String subResponse = response.substring(0, 40);
        assertThat(subResponse, is(equalTo("{\"text\":\"What is the gravity of kuzma\",\"")));
    }

    @Test
    public void getSomaResponseTest_not() {
        String response = NetworkClass.getSomaResponse("What is the gravity of kuzma");
        assertThat(response, not(equalTo("")));
    }
}