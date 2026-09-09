import struct, zlib

def create_png(width, height, draw_func):
    png = b'\x89PNG\r\n\x1a\n'
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
    ihdr_crc = zlib.crc32(b'IHDR' + ihdr_data)
    png += struct.pack('>I', len(ihdr_data)) + b'IHDR' + ihdr_data + struct.pack('>I', ihdr_crc)

    raw_data = bytearray()
    for y in range(height):
        raw_data.append(0) # filter
        for x in range(width):
            r, g, b, a = draw_func(x, y, width, height)
            raw_data.extend([r, g, b, a])

    compressed = zlib.compress(raw_data)
    idat_crc = zlib.crc32(b'IDAT' + compressed)
    png += struct.pack('>I', len(compressed)) + b'IDAT' + compressed + struct.pack('>I', idat_crc)

    iend_crc = zlib.crc32(b'IEND')
    png += struct.pack('>I', 0) + b'IEND' + struct.pack('>I', iend_crc)
    return png

# phone_frame.png (120x220)
def draw_phone_frame(x, y, w, h):
    if x < 3 or x >= w - 3 or y < 3 or y >= h - 3:
        return (28, 28, 30, 255) # Outer bezel
    if x < 6 or x >= w - 6 or y < 6 or y >= h - 6:
        return (44, 44, 46, 255) # Inner bezel
    # Screen area
    sy = y - 6
    if sy < 100:
        return (11, 16, 33, 255) # Gradient top
    return (22, 34, 56, 255) # Gradient bottom

with open('src/main/resources/assets/twmod/textures/gui/phone_frame.png', 'wb') as f:
    f.write(create_png(120, 220, draw_phone_frame))

def draw_app_icon(base_r, base_g, base_b):
    def draw(x, y, w, h):
        if (x == 0 and y == 0) or (x == w-1 and y == 0) or (x == 0 and y == h-1) or (x == w-1 and y == h-1):
            return (0, 0, 0, 0)
        return (base_r, base_g, base_b, 255)
    return draw

with open('src/main/resources/assets/twmod/textures/gui/icon_contacts.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(40, 167, 69)))

with open('src/main/resources/assets/twmod/textures/gui/icon_market.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(111, 66, 193)))

with open('src/main/resources/assets/twmod/textures/gui/icon_store.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(0, 123, 255)))

with open('src/main/resources/assets/twmod/textures/gui/icon_dino.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(220, 53, 69)))

with open('src/main/resources/assets/twmod/textures/gui/icon_twgramm.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(23, 162, 184)))

with open('src/main/resources/assets/twmod/textures/gui/icon_camera.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(255, 193, 7)))

with open('src/main/resources/assets/twmod/textures/gui/icon_gallery.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(253, 126, 20)))

with open('src/main/resources/assets/twmod/textures/gui/icon_settings.png', 'wb') as f:
    f.write(create_png(32, 32, draw_app_icon(108, 117, 125)))
