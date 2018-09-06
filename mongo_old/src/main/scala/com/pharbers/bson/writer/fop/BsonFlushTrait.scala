package com.pharbers.bson.writer.fop

import java.io.{File, OutputStream, RandomAccessFile}
import java.nio.{ByteBuffer, MappedByteBuffer}
import java.nio.channels.FileChannel
import java.util
import java.util.UUID

import org.bson.io.OutputBuffer
import org.bson._

trait BsonFlushTrait extends OutputBuffer {

    val path : String
    val bufferSize : Int

    var position = 0
//    var obj_count = 0

    lazy val buffer : Array[Byte] = new Array[Byte](bufferSize)
//    lazy val raf : RandomAccessFile = new RandomAccessFile(new File(path), "rw")
//    lazy val fc: FileChannel = raf.getChannel

    lazy val encode : BasicBSONEncoder = {
        val result = new BasicBSONEncoder()
        result.set(this)
        result
    }

    def closeFlush = {
        flushToPharbersFile
        encode.done()
    }

    def appendBsonObject(o : BSONObject) : Unit = {

        val cur = this.position
        try {
            encode.putObject(o)
//            obj_count = obj_count + 1

        } catch {
            case _ : java.lang.IllegalArgumentException => {
                this.position = cur
                encode.done()
                flushToPharbersFile
                encode.set(this)
                appendBsonObject(o)
            }
        }
    }


    def flushToPharbersFile = {
        val file = path + "/" + UUID.randomUUID() + ".bson"
        lazy val raf : RandomAccessFile = new RandomAccessFile(new File(file), "rw")
        lazy val fc: FileChannel = raf.getChannel

//        lazy val mem: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, raf.length(), position)
        lazy val mem: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, position)
        mem.put(buffer, 0, position)

        position = 0

        fc.close()
        raf.close()
    }

    override def write(b: Array[Byte]): Unit = {
        this.ensureOpen()
        this.write(b, 0, b.length)
    }

    override def writeBytes(bytes: Array[Byte], offset: Int, length: Int): Unit = {
        this.ensureOpen()
        this.ensure(length)
        System.arraycopy(bytes, offset, this.buffer, position, length)
        this.position += length
    }

    override def writeByte(value: Int): Unit = {
        this.ensureOpen()
        this.ensure(1)
        this.buffer(position)= (255 & value).toByte
        this.position += 1
    }

    override protected def write(absolutePosition : Int, value: Int) : Unit = {
        this.ensureOpen()
        if (absolutePosition < 0) throw new IllegalArgumentException(s"position must be >= 0 but was ${absolutePosition}")
        else if (absolutePosition > this.position - 1) throw new IllegalArgumentException(s"position must be <= ${this.position - 1} but was ${absolutePosition}")
        else this.buffer(absolutePosition) = (255 & value).toByte
    }

    override def getPosition : Int = {
        this.ensureOpen()
        this.position
    }

    override def getSize: Int = {
        this.ensureOpen()
        bufferSize
    }

    override def pipe(out: OutputStream) : Int = {
        this.ensureOpen()
        out.write(this.buffer, 0, this.position)
        this.position
    }

    override def truncateToPosition(newPosition: Int) : Unit = {
        println("trancateTo you mather fucker")
        this.ensureOpen()
        if (newPosition <= this.position && newPosition >= 0) this.position = newPosition
        else throw new IllegalArgumentException
    }

    override def getByteBuffers : util.List[ByteBuf] = {
        this.ensureOpen()
        util.Arrays.asList(new ByteBufNIO(ByteBuffer.wrap(this.buffer, 0, this.position).duplicate))
    }

    override def close() : Unit = closeFlush

    private def ensureOpen() : Unit = {
        if (this.buffer == null) throw new IllegalStateException("The output is closed")
    }

    private def ensure(more : Int): Unit = {
        if (this.position + more > this.buffer.length) {
            throw new java.lang.IllegalArgumentException()
        }
    }
}
