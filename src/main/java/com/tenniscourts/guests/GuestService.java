package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    public GuestDTO findGuestById(Long guestId) {
        return guestRepository.findById(guestId).map(guestMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
        });
    }

    public List<GuestDTO> findGuestByName(String name) {
        return guestMapper.map(guestRepository.findByNameContainingIgnoreCase(name));
    }

    public List<GuestDTO> findGuests() {
        return guestMapper.map(guestRepository.findAll());
    }


    public GuestDTO addGuest(CreateGuestDTO createGuestDTO) {
        return guestMapper.map(guestRepository.saveAndFlush(guestMapper.map(createGuestDTO)));
    }

    public GuestDTO updateGuest(GuestDTO guestDTO) {
        Guest guest = guestMapper.map(findGuestById(guestDTO.getId()));
        guest.setName(guestDTO.getName());
        return guestMapper.map(guestRepository.save(guest));
    }

    public void deleteGuest(Long guestId) {
        Guest guest =  guestMapper.map(findGuestById(guestId));
        guestRepository.delete(guest);
    }

}