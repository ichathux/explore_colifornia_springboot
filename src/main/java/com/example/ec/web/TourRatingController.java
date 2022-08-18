package com.example.ec.web;

import com.example.ec.domain.Tour;
import com.example.ec.domain.TourRating;
import com.example.ec.repo.TourRatingRepository;
import com.example.ec.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Tour Rating Controller
 *
 * Created by Mary Ellen Bowman
 */
@RestController
@RequestMapping(path = "/tours/{tourId}/ratings")
public class TourRatingController {
    TourRatingRepository tourRatingRepository;
    TourRepository tourRepository;

    @Autowired
    public TourRatingController(TourRatingRepository tourRatingRepository, TourRepository tourRepository) {
        this.tourRatingRepository = tourRatingRepository;
        this.tourRepository = tourRepository;
    }

    protected TourRatingController() {

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTourRating(@PathVariable( value = "tourId") String tourId,
                                 @RequestBody @Validated TourRating tourRating){
        verifyTour(tourId);
        tourRatingRepository.save(new TourRating(tourId, tourRating.getCustomerId(),
                tourRating.getScore(), tourRating.getComment()));
    }



    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public String return400(NoSuchElementException ex){
        return ex.getMessage();
    }

    @GetMapping
    public Page<TourRating> getAllRating(@PathVariable(value = "tourId") String tourId,
                                         Pageable pageable){

        return tourRatingRepository.findByTourId(tourId, pageable);
    }

    @GetMapping(value = "/average")
    public Map<String, Double> getAverage(@PathVariable(value = "tourId") String tourId){
        verifyTour(tourId);
        return Map.of("average",tourRatingRepository.findByTourId(tourId).stream()
                .mapToInt(TourRating::getScore)
                .average().orElseThrow(() -> new NoSuchElementException("Tour has no ratings.")));
    }

    @PutMapping
    public TourRating updateWithPut(@PathVariable(value = "tourId") String tourId,
                                   @RequestBody @Validated TourRating tourRating){
        TourRating rating = verifyTourRating(tourId, tourRating.getCustomerId());
        tourRating.setScore(tourRating.getScore());
        tourRating.setComment(tourRating.getComment());
        return tourRatingRepository.save(rating);
    }

    @PatchMapping
    public TourRating updateWithPatch(@PathVariable(value = "tourId") String tourId,
                                     @RequestBody @Validated TourRating tourRating){
        TourRating rating = verifyTourRating(tourId, tourRating.getCustomerId());
        if (tourRating.getScore() != null){
            rating.setScore(tourRating.getScore());
        }
        if (tourRating.getComment() != null){
            rating.setComment(tourRating.getComment());
        }
        return tourRatingRepository.save(rating);
    }

    @DeleteMapping(path = "/{customerId}")
    public void deleteRating(@PathVariable(value = "tourId") String tourId,
                             @PathVariable(value = "customerId") int customerId){

        TourRating tourRating = verifyTourRating(tourId, customerId);
        tourRatingRepository.delete(tourRating);
    }

    private TourRating verifyTourRating(String tourId,
                                        int customerId) {
        return tourRatingRepository.findByTourIdAndCustomerId(tourId,customerId)
                .orElseThrow(()-> new NoSuchElementException("Rating not exists tour ID "+tourId+" customer ID "+customerId));
    }

//    public void verifyTour(String tourId) throws NoSuchElementException{
//        if (!tourRepository.existsById(tourId)){
//            throw new NoSuchElementException("Tour does not exist");
//        }
//    }

    private Tour verifyTour(String tourId) throws NoSuchElementException{
        return tourRepository.findById(tourId).orElseThrow(() -> new NoSuchElementException(("Tour Id not exist "+ tourId)));
    }

}
