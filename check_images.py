import PIL.Image
import os

files = [
    '/workspaces/BubbleWrapper/DOWNLOADS/output-onlinepngtools (2).png',
    '/workspaces/BubbleWrapper/DOWNLOADS/output-onlinepngtools (4).png',
    '/workspaces/BubbleWrapper/DOWNLOADS/output-onlinepngtools (5).png',
    '/workspaces/BubbleWrapper/DOWNLOADS/IMG_9266.PNG',
    '/workspaces/BubbleWrapper/DOWNLOADS/image-1200x600.jpg'
]

for f in files:
    try:
        if os.path.exists(f):
            img = PIL.Image.open(f)
            print(f"{os.path.basename(f)}: {img.size}")
        else:
            print(f"{os.path.basename(f)}: Not Found")
    except Exception as e:
        print(f"{os.path.basename(f)}: Error {e}")
