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

from helpers import classify_image, read_labels, set_input_tensor

app = FastAPI()

# Settings
MIN_CONFIDENCE = 0.1  # The absolute lowest confidence for a detection.

# URL
STAGE_URL = "/v1/vision/stage"
SEGMENTATION_URL = "/v1/vision/seg"
# Models and labels

SCENE_MODEL = "models/classification/house-stage/model.tflite"
SCENE_LABELS = "models/classification/house-stage/labels.txt"
SEGMENTATION_MODEL = "models/segmentation/lite-model_deeplabv3-mobilenetv2-ade20k_1_default_1.tflite"
SEGMENTATION_LABELS = "models/segmentation/labels.txt"



stage_interpreter = tflite.Interpreter(model_path=SCENE_MODEL)
stage_interpreter.allocate_tensors()
stage_input_details = stage_interpreter.get_input_details()
stage_output_details = stage_interpreter.get_output_details()
stage_input_height = stage_input_details[0]["shape"][1]
stage_input_width = stage_input_details[0]["shape"][2]
stage_labels = read_labels(SCENE_LABELS)


interpreter = tflite.Interpreter(model_path=SEGMENTATION_MODEL)

# Set model input.
input_details = interpreter.get_input_details()
interpreter.allocate_tensors()

# get image size - converting from BHWC to WH
input_size = input_details[0]['shape'][2], input_details[0]['shape'][1]


@app.get("/")
async def info():
    return """tflite-server docs at ip:port/docs"""




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
        interpreter = tflite.Interpreter(model_path=SEGMENTATION_MODEL)
        # Invoke the interpreter to run inference.
        interpreter.allocate_tensors()
        interpreter.set_tensor(input_details[0]['index'], image_for_prediction)
        interpreter.invoke()
        # Retrieve the raw output map.
        raw_prediction = interpreter.tensor(interpreter.get_output_details()[0]['index'])()

        seg_map = np.argmax(raw_prediction,axis=3)
        print(seg_map)
        seg_map = np.squeeze(seg_map).astype(np.int8)
        #print(seg_map)
        seg_map=np.where((seg_map<0),0,seg_map)
        seg_map_list = seg_map.tolist()
        preds = {}
        preds["seg_map"] = seg_map_list
        preds["success"] = True
        return preds
        #preds = {"seg_map":seg_map}
        #print(dic)
        #return preds
    except:
    
        e = sys.exc_info()[1]
        raise HTTPException(status_code=500, detail=str(e))
