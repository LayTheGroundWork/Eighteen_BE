package com.st.eighteen_be.chat.api;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.facade.ChatroomFacade;
import com.st.eighteen_be.chat.service.kafka.ChattingProducer;
import com.st.eighteen_be.common.response.ApiResp;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.chat.api
 * fileName       : ChattingApiController
 * author         : ipeac
 * date           : 2024-03-29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-29        ipeac       최초 생성
 */
@Tag(name = "ChattingApiController", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChattingApiController {
    private final ChattingProducer chattingProducer;
    private final ChatroomFacade chatroomFacade;
    private final ChatroomService chatroomService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/api/v1/chat/all")
    public ApiResp<List<ChatroomWithLastestMessageDTO>> findAllMyChatrooms(@Valid @RequestBody FindChatRoomRequestDTO requestDTO) {
        log.info("findAllMyChatrooms.requestDTO.senderNo() = {}", requestDTO.senderNo());

        return ApiResp.success(HttpStatus.OK, chatroomService.findAllMyChatrooms(requestDTO));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "302", description = "INVALID REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/api/v1/chat/enter")
    //TODO 발신자 수신자 구조가 아니라, 채팅방 고유 Object_id를 받아서 처리하는 방식으로 변경해야 한다.
    public ApiResp<List<ChatMessageResponseDTO>> enterChatroom(@Valid @RequestBody EnterChatRoomRequestDTO requestDTO) {
        log.info("enterChatroom.requestDTO.senderNo() = {}", requestDTO.senderNo());
        log.info("enterChatroom.requestDTO.receiverNo() = {}", requestDTO.receiverNo());
        log.info("enterChatroom.requestDTO.requestTime() = {}", requestDTO.requestTime());

        return ApiResp.success(HttpStatus.OK, chatroomFacade.getOrCreateChatroom(requestDTO));
    }

    @MessageMapping("/v1/chat/{senderNo}/{receiverNo}/message") // /pub/chat/{senderNo}/{receiverNo}/message
    public void sendMessage(@DestinationVariable(value = "senderNo") Long senderNo, @DestinationVariable(value = "receiverNo") Long receiverNo, @RequestBody ChatMessageRequestDTO chatMessage) {
        log.info("sendMessage.senderNo() = {}, receiverNo() = {}", senderNo, receiverNo);
        log.info("sendMessage.chatMessage.message() = {}", chatMessage.getMessage());

        chattingProducer.send(KafkaConst.CHAT_TOPIC, chatMessage);
    }
}