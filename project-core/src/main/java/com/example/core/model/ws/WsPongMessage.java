package com.example.core.model.ws;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * WebSocket Pong 消息
 *
 * <h2>说明
 * <p>封装 WebSocket Pong 消息，用于心跳保活。
 * <p>声明为 final，不允许继承。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class WsPongMessage extends WsMessage {

}
