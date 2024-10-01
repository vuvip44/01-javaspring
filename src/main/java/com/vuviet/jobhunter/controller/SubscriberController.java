package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.entity.Subscriber;
import com.vuviet.jobhunter.repository.UserRepository;
import com.vuviet.jobhunter.service.SubscriberService;
import com.vuviet.jobhunter.service.UserService;
import com.vuviet.jobhunter.util.SecurityUtil;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import com.vuviet.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;



    public SubscriberController(SubscriberService subscriberService, UserRepository userRepository, UserService userService) {
        this.subscriberService = subscriberService;

    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody @Valid Subscriber subscriberDTO) throws IdInvalidException {

        if(this.subscriberService.isExistEmail(subscriberDTO.getEmail())){
            throw new IdInvalidException("Email "+subscriberDTO.getEmail()+ " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriberDTO));
    }

    @PutMapping("/subscribers")
    @ApiMessage("update a subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriberDTO) throws IdInvalidException{
        Subscriber subscriber=this.subscriberService.getById(subscriberDTO.getId());
        if(subscriber==null){
            throw new IdInvalidException("Id "+subscriberDTO.getId()+" không tồn tại");
        }
        return ResponseEntity.ok(this.subscriberService.update(subscriberDTO));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException{
        String email= SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok(this.subscriberService.getByEmail(email));
    }
}
