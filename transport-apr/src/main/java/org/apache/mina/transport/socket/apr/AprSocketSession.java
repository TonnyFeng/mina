/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.transport.socket.apr;

import java.net.InetSocketAddress;

import org.apache.mina.common.DefaultTransportMetadata;
import org.apache.mina.common.IoBuffer;
import org.apache.mina.common.IoProcessor;
import org.apache.mina.common.IoService;
import org.apache.mina.common.RuntimeIoException;
import org.apache.mina.common.TransportMetadata;
import org.apache.mina.transport.socket.AbstractSocketSessionConfig;
import org.apache.mina.transport.socket.SocketSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.tomcat.jni.Socket;

/**
 * TODO Add documentation
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev$, $Date$
 */
class AprSocketSession extends AprSession implements SocketSession {

    static final TransportMetadata METADATA =
        new DefaultTransportMetadata(
                "apr", "socket", false, true,
                InetSocketAddress.class,
                SocketSessionConfig.class,
                IoBuffer.class);

    private final SocketSessionConfig config = new SessionConfigImpl();
    
    AprSocketSession(
            IoService service, IoProcessor<AprSession> processor, long descriptor) throws Exception {
        super(service, processor, descriptor);
        this.config.setAll(service.getSessionConfig());
    }


    public SocketSessionConfig getConfig() {
        return config;
    }

    public TransportMetadata getTransportMetadata() {
        return METADATA;
    }


    private class SessionConfigImpl extends AbstractSocketSessionConfig {

        public boolean isKeepAlive() {
            try {
                return Socket.optGet(getDescriptor(), Socket.APR_SO_KEEPALIVE) == 1;
            } catch (Exception e) {
                throw new RuntimeIoException("Failed to get SO_KEEPALIVE.", e);
            }
        }

        public void setKeepAlive(boolean on) {
            Socket.optSet(getDescriptor(), Socket.APR_SO_KEEPALIVE, on ? 1 : 0);
        }

        public boolean isOobInline() {
            return false;
        }

        public void setOobInline(boolean on) {
        }

        public boolean isReuseAddress() {
            try {
                return Socket.optGet(getDescriptor(), Socket.APR_SO_REUSEADDR) == 1;
            } catch (Exception e) {
                throw new RuntimeIoException("Failed to get SO_REUSEADDR.", e);
            }
        }

        public void setReuseAddress(boolean on) {
            Socket.optSet(getDescriptor(), Socket.APR_SO_REUSEADDR, on ? 1 : 0);
        }

        public int getSoLinger() {
            try {
                return Socket.optGet(getDescriptor(), Socket.APR_SO_LINGER);
            } catch (Exception e) {
                throw new RuntimeIoException("Failed to get SO_LINGER.", e);
            }
        }

        public void setSoLinger(int linger) {
            // TODO: Figure out how to disable this.
            Socket.optSet(getDescriptor(), Socket.APR_SO_LINGER, linger);
        }

        public boolean isTcpNoDelay() {
            try {
                return Socket.optGet(getDescriptor(), Socket.APR_TCP_NODELAY) == 1;
            } catch (Exception e) {
                throw new RuntimeIoException("Failed to get TCP_NODELAY.", e);
            }
        }

        public void setTcpNoDelay(boolean on) {
            Socket.optSet(getDescriptor(), Socket.APR_TCP_NODELAY, on ? 1 : 0);
        }

        public int getTrafficClass() {
            return 0;
        }

        public void setTrafficClass(int tc) {
        }

        public int getSendBufferSize() {
            try {
                return Socket.optGet(getDescriptor(), Socket.APR_SO_SNDBUF);
            } catch (Exception e) {
                throw new RuntimeException("APR Exception", e);
            }
        }

        public void setSendBufferSize(int size) {
            Socket.optSet(getDescriptor(), Socket.APR_SO_SNDBUF, size);
        }

        public int getReceiveBufferSize() {
            try {
                return Socket.optGet(getDescriptor(), Socket.APR_SO_RCVBUF);
            } catch (Exception e) {
                throw new RuntimeException("APR Exception", e);
            }
        }

        public void setReceiveBufferSize(int size) {
            Socket.optSet(getDescriptor(), Socket.APR_SO_RCVBUF, size);
        }
    }
}
