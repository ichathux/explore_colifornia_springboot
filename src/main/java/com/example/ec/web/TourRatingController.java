package com.example.ec.web;

import com.example.ec.domain.Tour;
import com.example.ec.domain.TourRating;
import com.example.ec.domain.TourRatingPk;
import com.example.ec.repo.TourRatingRepository;
import com.example.ec.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
    public void createTourRating(@PathVariable( value = "tourId") int tourId,
                                 @RequestBody @Validated RatingDto ratingDto){
        Tour tour = verifyTour(tourId);
        tourRatingRepository.save(new TourRating(new TourRatingPk(tour, ratingDto.getCustomerId()),
                ratingDto.getScore(), ratingDto.getComment()));
    }

    private Tour verifyTour(int tourId) throws NoSuchElementException{
        return tourRepository.findById(tourId).orElseThrow(() -> new NoSuchElementException(("Tour Id not exist "+ tourId)));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public String return400(NoSuchElementException ex){
        return ex.getMessage();
    }

    @GetMapping
    public List<TourRating> getAllRating(@PathVariable(value = "tourId") int tourId){
        Tour tour = verifyTour(tourId);
        return tourRatingRepository.findByPkTourId(tourId);
    }

    @GetMapping(value = "/average")
    public Map<String, Double> getAverage(@PathVariable(value = "tourId") int tourId){
        verifyTour(tourId);
        return Map.of("average",tourRatingRepository.findByPkTourId(tourId).stream()
                .mapToInt(TourRating::getScore)
                .average().orElseThrow(() -> new NoSuchElementException("Tour has no ratings.")));
    }

    @PutMapping
    public RatingDto updateWithPut(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto){
        TourRating tourRating = verifyTourRating(tourId, ratingDto.getCustomerId());
        tourRating.setScore(ratingDto.getScore());
        tourRating.setComment(ratingDto.getComment());
        return new RatingDto(tourRatingRepository.save(tourRating));
    }

    @PatchMapping
    public RatingDto updateWithPatch(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto){
        TourRating tourRating = verifyTourRating(tourId, ratingDto.getCustomerId());
        if (ratingDto.getScore() != null){
            tourRating.setScore(ratingDto.getScore());
        }
        if (ratingDto.getComment() != null){
            tourRating.setComment(ratingDto.getComment());
        }
        return new RatingDto(tourRatingRepository.save(tourRating));
    }

    @DeleteMapping(path = "/{customerId}")
    public void deleteRating(@PathVariable(value = "tourId") int tourId, @PathVariable(value = "customerId") int customerId){
        TourRating tourRating = verifyTourRating(tourId, customerId);
        tourRatingRepository.delete(tourRating);

    }

    private TourRating verifyTourRating(int tourId, int customerId) {
        return tourRatingRepository.findByPkTourIdAndPkCustomerId(tourId,customerId)
                .orElseThrow(()-> new NoSuchElementException("Rating not exists tour ID "+tourId+" customer ID "+customerId));
    }

}
