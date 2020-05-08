package com.viettel.backend.restful;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.engine.file.DbFile;
import com.viettel.backend.engine.file.DbFileMeta;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.DefaultSignedRequestConverter.TicketInfo;
import com.viettel.backend.oauth2.core.SecurityContextHelper;
import com.viettel.backend.oauth2.core.SignedRequestConverter;
import com.viettel.backend.oauth2.core.TicketResult;

@RestController
public class FileController extends AbstractController {

    // 01/01/2010 00:00:00
    private static final long lastModified = 1262322000000l;

    @Autowired
    private FileEngine fileEngine;

    @Autowired
    private SignedRequestConverter signedRequestConverter;

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(@RequestParam(value = "file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        String id = fileEngine.store(SecurityContextHelper.getCurrentUser(), inputStream,
                file.getOriginalFilename(), file.getContentType(), new DbFileMeta());
        IOUtils.closeQuietly(inputStream);
        
        return new Envelope(new IdDto(id)).toResponseEntity();
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET, headers = { HttpHeaders.AUTHORIZATION })
    public ResponseEntity<?> getFileTicket(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "id") String id) throws IOException {
        // Validate permission if necessary
        String ticket = signedRequestConverter.signRequest(request, response, 5L * 60 * 1000, getUserLogin());
        return new ResponseEntity<TicketResult>(new TicketResult(ticket), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET, params = { OAuth2AccessToken.ACCESS_TOKEN })
    public void getFileRedirect(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "id") String id) throws IOException {
        // Validate permission if necessary
        signedRequestConverter.signRequest(request, response, 5L * 60 * 1000, getUserLogin());
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET, params = { SignedRequestConverter.PARAMETER_TICKET })
    public ResponseEntity<?> getFileDownload(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "id") String id) throws IOException {

        TicketInfo ticketInfo = signedRequestConverter.verifyRequest(request, response);

        DbFile file = fileEngine.get(ticketInfo.toUserLogin(), id);
        BusinessAssert.notNull(file, "file not found");

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType(file.getContentType()));
        header.set("Content-Disposition", "inline; filename=\"" + file.getFileName() + "\"");
        header.set("Cache-Control", "public, max-age=31556926");
        header.set("ETag", id);
        header.setDate("Last-Modified", lastModified);

        ResponseEntity<InputStreamResource> result = new ResponseEntity<InputStreamResource>(
                new InputStreamResource(file.getInputStream()), header, HttpStatus.OK);

        return result;
    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFile(@RequestParam(value = "id") String id) throws IOException {
        fileEngine.delete(SecurityContextHelper.getCurrentUser(), id);
        return new Envelope(Meta.OK).toResponseEntity();
    }

    // IMAGE
    @RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getImage(@PathVariable(value = "id") String id) throws IOException {
        DbFile file = fileEngine.getImage(id);
        BusinessAssert.notNull(file, "image not found");

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.IMAGE_JPEG);
        header.set("Content-Disposition", "inline; filename=\"" + file.getFileName() + "\"");
        header.set("Cache-Control", "public, max-age=31556926");
        header.set("ETag", id);
        header.setDate("Last-Modified", lastModified);

        ResponseEntity<InputStreamResource> result = new ResponseEntity<InputStreamResource>(
                new InputStreamResource(file.getInputStream()), header, HttpStatus.OK);

        return result;
    }

    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(@RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "sizetype", required = false) String sizetype) throws IOException {
        InputStream inputStream = file.getInputStream();
        String id = fileEngine.storeImage(inputStream, file.getOriginalFilename(), file.getContentType(),
                new DbFileMeta(), sizetype);
        IOUtils.closeQuietly(inputStream);
        
        return new Envelope(new IdDto(id)).toResponseEntity();
    }
}
