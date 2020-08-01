# Backend
- This branch contains code of the backend of our app.
- For main android source code , head over to [master branch](https://github.com/ucalyptus/NC_SVCE_MK200_Stargazers) (default)

## Usage

Start the tflite-server on port 5000 :

```shell
$ uvicorn tflite-server:app --reload --port 5000 --host 0.0.0.0
```

You can check that the tflite-server is running by visiting `http://ip:5000/` from any machine, where `ip` is the ip address of the host (`localhost` if querying from the same machine). The docs can be viewed at `http://localhost:5000/docs`

Post an image to detecting objects via cURL:

```shell
curl -X POST "http://localhost:5000/v1/vision/stage" -H  "accept: application/json" -H  "Content-Type: multipart/form-data" -F "image=@tests/13.jpg;type=image/jpeg"
```

