package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Skill;
import com.vuviet.jobhunter.entity.Subscriber;
import com.vuviet.jobhunter.entity.User;
import com.vuviet.jobhunter.repository.SkillRepository;
import com.vuviet.jobhunter.repository.SubscriberRepository;
import com.vuviet.jobhunter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface SubscriberService {
    Subscriber create(Subscriber subscriberDTO);

    Subscriber update(Subscriber subscriberDTO);

    Subscriber getById(long id);

    boolean isExistEmail(String email);
}

@Service
class SubscriberServiceImpl implements SubscriberService{
    private final SubscriberRepository subscriberRepository;

    private final SkillRepository skillRepository;



    SubscriberServiceImpl(SubscriberRepository subscriberRepository, SkillRepository skillRepository, UserRepository userRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;

    }

    @Override
    public Subscriber create(Subscriber subscriberDTO) {
        if(subscriberDTO.getSkills()!=null){
            List<Long> reqSkills=subscriberDTO.getSkills()
                    .stream().map(item->item.getId()).toList();

            List<Skill> dbSkills=this.skillRepository.findByIdIn(reqSkills);
            subscriberDTO.setSkills(dbSkills);
        }

        return this.subscriberRepository.save(subscriberDTO);
    }

    @Override
    public Subscriber update(Subscriber subscriberDTO) {
        Optional<Subscriber> subscriberOptional=this.subscriberRepository.findById(subscriberDTO.getId());
        if(subscriberOptional.isPresent()){
            Subscriber subscriber=subscriberOptional.get();
            if(subscriberDTO.getSkills()!=null){
                List<Long> reqSkills=subscriberDTO.getSkills()
                        .stream().map(x->x.getId()).toList();
                List<Skill> dbSkills=this.skillRepository.findByIdIn(reqSkills);
                subscriber.setSkills(dbSkills);
            }
            this.subscriberRepository.save(subscriber);
            return subscriber;
        }

        return null;
    }

    @Override
    public Subscriber getById(long id) {
        Optional<Subscriber> subscriber=this.subscriberRepository.findById(id);
        if(subscriber.isPresent()){
            return subscriber.get();
        }
        return null;
    }

    @Override
    public boolean isExistEmail(String email) {

        return this.subscriberRepository.existsByEmail(email);
    }
}
