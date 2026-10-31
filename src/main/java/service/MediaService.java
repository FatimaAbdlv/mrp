package service;

import model.MediaEntry;
import repository.MediaRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public MediaEntry create(MediaEntry media, int creatorId) throws SQLException {
        checkMedia(media);
        media.setId(null);
        media.setCreatorId(creatorId);
        return mediaRepository.save(media);
    }

    public List<MediaEntry> getAll() throws SQLException {
        return mediaRepository.findAll();
    }

    public MediaEntry getById(int id) throws SQLException {
        MediaEntry media = mediaRepository.findById(id);
        if (media == null) {
            throw new ServiceException(404, "Medium nicht gefunden");
        }
        return media;
    }

    public MediaEntry update(int id, MediaEntry media, int userId) throws SQLException {
        MediaEntry existing = findOwned(id, userId);
        checkMedia(media);
        media.setId(existing.getId());
        media.setCreatorId(existing.getCreatorId());
        mediaRepository.update(media);
        return media;
    }

    public void delete(int id, int userId) throws SQLException {
        findOwned(id, userId);
        mediaRepository.delete(id);
    }

    private MediaEntry findOwned(int id, int userId) throws SQLException {
        MediaEntry media = mediaRepository.findById(id);
        if (media == null) {
            throw new ServiceException(404, "Medium nicht gefunden");
        }
        if (media.getCreatorId() != userId) {
            throw new ServiceException(403, "Nur der Ersteller darf dieses Medium ändern oder löschen");
        }
        return media;
    }

    private void checkMedia(MediaEntry media) {
        if (media.getTitle() == null || media.getTitle().isBlank()) {
            throw new ServiceException(400, "Titel ist Pflicht");
        }
        if (media.getMediaType() == null) {
            throw new ServiceException(400, "Medientyp ist Pflicht");
        }
        if (media.getReleaseYear() <= 0) {
            throw new ServiceException(400, "Erscheinungsjahr ist ungültig");
        }
        if (media.getAgeRestriction() < 0) {
            throw new ServiceException(400, "Altersfreigabe ist ungültig");
        }
        if (media.getGenres() == null) {
            media.setGenres(new ArrayList<>());
        }
    }
}