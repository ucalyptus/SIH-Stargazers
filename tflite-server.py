"""
Expose tflite models via a rest API.
"""
import io
import sys

import numpy as np
import tflite_runtime.interpreter as tflite
from fastapi import FastAPI, File, HTTPException, UploadFile
from PIL import Image
from PIL import ImageOps
import tensorflow as tf
from helpers import classify_image, read_labels, set_input_tensor

app = FastAPI()

# Settings
MIN_CONFIDENCE = 0.1  # The absolute lowest confidence for a detection.
# URL
# FACE_DETECTION_URL = "/v1/vision/face"
# OBJ_DETECTION_URL = "/v1/vision/detection"
STAGE_URL = "/v1/vision/stage"
# Models and labels
# FACE_MODEL = "models/face_detection/mobilenet_ssd_v2_face/mobilenet_ssd_v2_face_quant_postprocess.tflite"
# OBJ_MODEL = "models/object_detection/mobilenet_ssd_v2_coco/mobilenet_ssd_v2_coco_quant_postprocess.tflite"
# OBJ_LABELS = "models/object_detection/mobilenet_ssd_v2_coco/coco_labels.txt"
SCENE_MODEL = "models/classification/house-stage/model.tflite"
SCENE_LABELS = "models/classification/house-stage/labels.txt"
SEGMENTATION_MODEL = "models/Segmentation/lite-model_deeplabv3-mobilenetv2-ade20k_1_default_1.tflite"
SEGMENTATION_LABELS = "models/Segmentation/labels.txt"
# Setup object detection
# obj_interpreter = tflite.Interpreter(model_path=OBJ_MODEL)
# obj_interpreter.allocate_tensors()
# obj_input_details = obj_interpreter.get_input_details()
# obj_output_details = obj_interpreter.get_output_details()
# obj_input_height = obj_input_details[0]["shape"][1]
# obj_input_width = obj_input_details[0]["shape"][2]
# obj_labels = read_labels(OBJ_LABELS)

# Setup face detection
# face_interpreter = tflite.Interpreter(model_path=FACE_MODEL)
# face_interpreter.allocate_tensors()
# face_input_details = face_interpreter.get_input_details()
# face_output_details = face_interpreter.get_output_details()
# face_input_height = face_input_details[0]["shape"][1]
# face_input_width = face_input_details[0]["shape"][2]

# Setup face detection
stage_interpreter = tflite.Interpreter(model_path=SCENE_MODEL)
stage_interpreter.allocate_tensors()
stage_input_details = stage_interpreter.get_input_details()
stage_output_details = stage_interpreter.get_output_details()
stage_input_height = stage_input_details[0]["shape"][1]
stage_input_width = stage_input_details[0]["shape"][2]
stage_labels = read_labels(SCENE_LABELS)


@app.get("/")
async def info():
    return """tflite-server docs at ip:port/docs"""


# @app.post(FACE_DETECTION_URL)
# async def predict_face(image: UploadFile = File(...)):
#     try:
#         contents = await image.read()
#         image = Image.open(io.BytesIO(contents))
#         image_width = image.size[0]
#         image_height = image.size[1]

#         # Format data and send to interpreter
#         resized_image = image.resize((face_input_width, face_input_height), Image.ANTIALIAS)
#         input_data = np.expand_dims(resized_image, axis=0)
#         face_interpreter.set_tensor(face_input_details[0]["index"], input_data)

#         # Process image and get predictions
#         face_interpreter.invoke()
#         boxes = face_interpreter.get_tensor(face_output_details[0]["index"])[0]
#         classes = face_interpreter.get_tensor(face_output_details[1]["index"])[0]
#         scores = face_interpreter.get_tensor(face_output_details[2]["index"])[0]

#         data = {}
#         faces = []
#         for i in range(len(scores)):
#             if not classes[i] == 0:  # Face
#                 continue
#             single_face = {}
#             single_face["userid"] = "unknown"
#             single_face["confidence"] = float(scores[i])
#             single_face["y_min"] = int(float(boxes[i][0]) * image_height)
#             single_face["x_min"] = int(float(boxes[i][1]) * image_width)
#             single_face["y_max"] = int(float(boxes[i][2]) * image_height)
#             single_face["x_max"] = int(float(boxes[i][3]) * image_width)
#             if single_face["confidence"] < MIN_CONFIDENCE:
#                 continue
#             faces.append(single_face)

#         data["predictions"] = faces
#         data["success"] = True
#         return data
#     except:
#         e = sys.exc_info()[1]
#         raise HTTPException(status_code=500, detail=str(e))


# @app.post(OBJ_DETECTION_URL)
# async def predict_object(image: UploadFile = File(...)):
#     try:
#         contents = await image.read()
#         image = Image.open(io.BytesIO(contents))
#         image_width = image.size[0]
#         image_height = image.size[1]

#         # Format data and send to interpreter
#         resized_image = image.resize((obj_input_width, obj_input_height), Image.ANTIALIAS)
#         input_data = np.expand_dims(resized_image, axis=0)
#         obj_interpreter.set_tensor(obj_input_details[0]["index"], input_data)

#         # Process image and get predictions
#         obj_interpreter.invoke()
#         boxes = obj_interpreter.get_tensor(obj_output_details[0]["index"])[0]
#         classes = obj_interpreter.get_tensor(obj_output_details[1]["index"])[0]
#         scores = obj_interpreter.get_tensor(obj_output_details[2]["index"])[0]

#         data = {}
#         objects = []
#         for i in range(len(scores)):
#             single_object = {}
#             single_object["label"] = obj_labels[int(classes[i])]
#             single_object["confidence"] = float(scores[i])
#             single_object["y_min"] = int(float(boxes[i][0]) * image_height)
#             single_object["x_min"] = int(float(boxes[i][1]) * image_width)
#             single_object["y_max"] = int(float(boxes[i][2]) * image_height)
#             single_object["x_max"] = int(float(boxes[i][3]) * image_width)

#             if single_object["confidence"] < MIN_CONFIDENCE:
#                 continue
#             objects.append(single_object)

#         data["predictions"] = objects
#         data["success"] = True
#         return data
#     except:
#         e = sys.exc_info()[1]
#         raise HTTPException(status_code=500, detail=str(e))


@app.post(STAGE_URL)
async def predict_stage(image: UploadFile = File(...)):
    try:
        contents = await image.read()
        image = Image.open(io.BytesIO(contents))
        resized_image = image.resize((stage_input_width, stage_input_height), Image.ANTIALIAS)
        results = classify_image(stage_interpreter, image=resized_image)
        label_id, prob = results[0]
        data = {}
        data["label"] = stage_labels[label_id]
        data["confidence"] = prob
        data["success"] = True
        return data
    except:
        e = sys.exc_info()[1]
        raise HTTPException(status_code=500, detail=str(e))
        

@app.post(SEGMENTATION_URL)
async def predict_segment(image: UploadFile = File(...)):
    try:
        contents = await image.read()
        image = Image.open(io.BytesIO(contents))
        
        old_size = image.size  # old_size is in (width, height) format
        desired_ratio = input_size[0] / input_size[1]
        old_ratio = old_size[0] / old_size[1]
        if old_ratio < desired_ratio: # '<': cropping, '>': padding
        	new_size = (old_size[0], int(old_size[0] / desired_ratio))
        else:
        	new_size = (int(old_size[1] * desired_ratio), old_size[1])
        # Cropping the original image to the desired aspect ratio
        delta_w = new_size[0] - old_size[0]
        delta_h = new_size[1] - old_size[1]
        padding = (delta_w//2, delta_h//2, delta_w-(delta_w//2), delta_h-(delta_h//2))
        cropped_image = ImageOps.expand(image, padding)
        resized_image = cropped_image.convert('RGB').resize(input_size, Image.BILINEAR)
        # Convert to a NumPy array, add a batch dimension, and normalize the image.
        image_for_prediction = np.asarray(resized_image).astype(np.float32)
        image_for_prediction = np.expand_dims(image_for_prediction, 0)
        image_for_prediction = image_for_prediction / 127.5 - 1
        # Load the model.
        interpreter = tf.lite.Interpreter(model_path=SEGMENTATION_MODEL)
        # Invoke the interpreter to run inference.
        interpreter.allocate_tensors()
        interpreter.set_tensor(input_details[0]['index'], image_for_prediction)
        interpreter.invoke()
        # Retrieve the raw output map.
        raw_prediction = interpreter.tensor(interpreter.get_output_details()[0]['index'])()
        #print(raw_prediction[0])
        width, height = cropped_image.size
        seg_map = tf.argmax(tf.image.resize(raw_prediction, (height, width)), axis=3)
        seg_map = tf.squeeze(seg_map).numpy().astype(np.int8)
        
        #return seg_map"""
        preds = {"seg_map":seg_map}
        #print(dic)
        return preds
    except:
    
        e = sys.exc_info()[1]
        raise HTTPException(status_code=500, detail=str(e))
