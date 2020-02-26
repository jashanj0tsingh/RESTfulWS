package com.jashanjotsingh.restfulws.resources;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author jashanjotsingh
 */
@Path("demos/files")
public class FileDemo {
    // upload an image say (foo.jpeg) to the server
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response imageUpload(@FormDataParam("image") InputStream image,
            @FormDataParam("image") FormDataContentDisposition metadata)
            throws Exception {
        String path = "/tmp/";
        String location = "";
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream output = new FileOutputStream(new File(path + metadata.getFileName()));
            location = path + metadata.getFileName();
            while ((read = image.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            output.flush();
        } catch (IOException e) {
            throw new WebApplicationException("Error while uploading image!");
        }
        String response = "Image was uploaded successfully at " + location;
        return Response.status(200).entity(response).build();
    }
    // convert uploaded image (foo.jpeg) to grayscale and download it
    @GET
    @Path("download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response imageDownload() throws IOException {
        File file = new File("/tmp/foo.jpeg");
        BufferedImage color = ImageIO.read(file);
        int width = color.getWidth();
        int height = color.getHeight();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                Color c = new Color(color.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color grayscale = new Color(red + green + blue,
                                            red + green + blue, 
                                            red + green + blue);
                color.setRGB(j, i, grayscale.getRGB());
            }
        }
        File grayscale = new File("mono-" + file.getName());
        ImageIO.write(color, "jpg", grayscale);
        ResponseBuilder response = Response.ok((Object) grayscale);
        response.header("Content-Disposition", "attachment; filename=" + grayscale.getName());
        return response.build();
    }
}
