package com.panda.merge.mq.build;

import java.util.Map;

public interface BusinessMessageBuilder {
      Map<String,Object> buildBusinessMessage(String json);
}
