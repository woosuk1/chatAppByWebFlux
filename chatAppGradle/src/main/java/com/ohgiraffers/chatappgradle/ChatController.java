package com.ohgiraffers.chatappgradle;

import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@RestController // 데이터 리턴 서버
public class ChatController {

    private final ChatRepository chatRepository;

    @CrossOrigin
    /* 설명.
     *  TEXT_EVENT_STREAM_VALUE -> SSE 프로토콜을 열어주는 것
     *  귓속말(1대1 채팅할 때 사용할 것)
    * */
    @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMsg(@PathVariable String sender, @PathVariable String receiver) {
        return chatRepository.mFindBySender(sender, receiver)
                .subscribeOn(Schedulers.boundedElastic());
    }

    /* 설명. roomNum을 숨길 수 있으면 좋겠다. -> openchatting이다.(방번호를 링크로 보내주면) */
    @CrossOrigin
    @GetMapping(value = "/chat/roomNum/{roomNum}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> findByRoomNum(@PathVariable Integer roomNum) {
        return chatRepository.mFindByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @CrossOrigin
    @PostMapping("/chat")
    /* 설명. FLUX 타입은 계속 return, Mono 타입은 한번만 보여준다.(의도에 따라) */
    public Mono<Chat> setMsg(@RequestBody Chat chat){
        /* 설명. 현재 시간은 back에서 설정 */
        chat.setCreatedAt(LocalDateTime.now());
        return chatRepository.save(chat); // Object(chat)를 리턴하면 자동으로 JSON 변환 (MessageConverter)
    }
}
