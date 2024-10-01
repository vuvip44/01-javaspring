package com.vuviet.jobhunter.service;

import com.vuviet.jobhunter.entity.Job;
import com.vuviet.jobhunter.entity.Skill;
import com.vuviet.jobhunter.entity.Subscriber;
import com.vuviet.jobhunter.entity.response.ResEmailJob;
import com.vuviet.jobhunter.repository.JobRepository;
import com.vuviet.jobhunter.repository.SkillRepository;
import com.vuviet.jobhunter.repository.SubscriberRepository;
import com.vuviet.jobhunter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SubscriberService {
    Subscriber create(Subscriber subscriberDTO);

    Subscriber update(Subscriber subscriberDTO);

    Subscriber getById(long id);

    boolean isExistEmail(String email);

    void sendSubscribersEmailJobs();

    ResEmailJob convertJobToSendEmail(Job job);

    Subscriber getByEmail(String email);
}

@Service
class SubscriberServiceImpl implements SubscriberService{
    private final SubscriberRepository subscriberRepository;

    private final SkillRepository skillRepository;

    private final JobRepository jobRepository;

    private final EmailService emailService;

    SubscriberServiceImpl(SubscriberRepository subscriberRepository, SkillRepository skillRepository, UserRepository userRepository, JobRepository jobRepositoryl, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;

        this.jobRepository = jobRepository;
        this.emailService = emailService;
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

    @Override
    public void sendSubscribersEmailJobs() {
        List<Subscriber> subscribers=this.subscriberRepository.findAll();
        if(subscribers!=null && subscribers.size()>0){
            for(Subscriber sub:subscribers){
                List<Skill> skills=sub.getSkills();
                if(skills!=null && skills.size()>0){
                    List<Job> jobs=this.jobRepository.findBySkillsIn(skills);
                    if(jobs!=null && jobs.size()>0){

                        List<ResEmailJob> arr=jobs.stream().map(
                                job -> this.convertJobToSendEmail(job)
                        ).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr
                        );
                    }
                }
            }
        }
    }

    @Override
    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res=new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));

        List<Skill> skills=job.getSkills();
        List<ResEmailJob.SkillEmail> s=skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName())).collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    @Override
    public Subscriber getByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }


}
