package com.leeo.kafka;

import com.alibaba.fastjson.JSON;
//import org.linuxsogood.qilian.enums.CupMessageType;
//import org.linuxsogood.qilian.kafka.MessageWrapper;
//import org.linuxsogood.qilian.model.store.Store;
//import org.linuxsogood.sync.mapper.StoreMapper;
//import org.linuxsogood.sync.model.StoreExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.Optional;

public class Listener {

	private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
 
//    @Autowired
//    private StoreMapper storeMapper;
 
    /**
     * 监听kafka消息,如果有消息则消费,同步数据到新烽火的库
     * @param record 消息实体bean
     */
 @KafkaListener(topics = "test", group = "test-consumer-group")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
            	System.out.println("----: " + JSON.toJSONString(message));
//                MessageWrapper messageWrapper = JSON.parseObject(message.toString(), MessageWrapper.class);
//                CupMessageType type = messageWrapper.getType();
//                //判断消息的数据类型,不同的数据入不同的表
//                if (CupMessageType.STORE == type) {
//                    proceedStore(messageWrapper);
//                }
            } catch (Exception e) {
                LOGGER.error("将接收到的消息保存到数据库时异常, 消息:{}, 异常:{}",message.toString(),e);
            }
        }
 }

	/**
     * 消息是店铺类型,店铺消息处理入库
     * @param messageWrapper 从kafka中得到的消息
     */
//    private void proceedStore(MessageWrapper messageWrapper) {
//        Object data = messageWrapper.getData();
//        Store cupStore = JSON.parseObject(data.toString(), Store.class);
//        StoreExample storeExample = new StoreExample();
//        String storeName = StringUtils.isBlank(cupStore.getStoreOldName()) ? cupStore.getStoreName() : cupStore.getStoreOldName();
//        storeExample.createCriteria().andStoreNameEqualTo(storeName);
//        List<org.linuxsogood.sync.model.Store> stores = storeMapper.selectByExample(storeExample);
//        org.linuxsogood.sync.model.Store convertStore = new org.linuxsogood.sync.model.Store();
//        org.linuxsogood.sync.model.Storestore = convertStore.convert(cupStore);
//        //如果查询不到记录则新增
//        if (stores.size() == 0) {
//            storeMapper.insert(store);
//        } else {
//            store.setStoreId(stores.get(0).getStoreId());
//            storeMapper.updateByPrimaryKey(store);
//        }
//    }
 
}