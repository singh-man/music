# ocr_pdf.py
# Debian/Ubuntu
# sudo apt update
# sudo apt install -y poppler-utils tesseract-ocr tesseract-ocr-hin
# (on macOS use `brew install poppler tesseract` and add hindi language if needed)
# pip install pdf2image pillow pytesseract
# python ocr_pdf.py
from pdf2image import convert_from_path
from PIL import Image
import pytesseract
from pathlib import Path

pdf_path = Path("रामराज्य - आशुतोष राना.pdf")   # put the PDF in same folder
out_txt = Path("रामराज्य_आशुतोष_राना_ocr.txt")

pages = convert_from_path(str(pdf_path), dpi=200)
all_text = []
for i, img in enumerate(pages, start=1):
    text = ""
    try:
        text = pytesseract.image_to_string(img, lang='hin+eng')
    except Exception:
        text = pytesseract.image_to_string(img, lang='eng')
    all_text.append(f"\n\n--- PAGE {i} ---\n\n" + text)

out_txt.write_text("".join(all_text), encoding="utf-8")
print("Saved:", out_txt)
