package com.banque.msoc.controller;

import com.banque.msoc.dto.notification.OcNotificationDto;
import com.banque.msoc.service.OcNotificationPersistenceService;
import com.banque.msoc.service.OcNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/oc/notifications")
@RequiredArgsConstructor
public class OcNotificationController {

    private final OcNotificationService sseService;
    private final OcNotificationPersistenceService persistenceService;

    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter stream() {
        return sseService.subscribe();
    }

    @GetMapping
    public Page<OcNotificationDto> list(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return persistenceService.findNotifications(
                unreadOnly,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @GetMapping("/unread-count")
    public long unreadCount() {
        return persistenceService.countUnread();
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        persistenceService.markAsRead(id);
    }

    @PatchMapping("/read-all")
    public void markAllAsRead() {
        persistenceService.markAllAsRead();
    }
}
